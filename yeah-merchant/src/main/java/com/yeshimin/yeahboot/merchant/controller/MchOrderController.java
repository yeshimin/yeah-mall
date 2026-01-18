package com.yeshimin.yeahboot.merchant.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.yeshimin.yeahboot.auth.common.config.security.PublicAccess;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.controller.validation.Query;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.mapper.OrderMapper;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.merchant.common.properties.Kuaidi100Properties;
import com.yeshimin.yeahboot.merchant.domain.dto.Kd100CallbackParam;
import com.yeshimin.yeahboot.merchant.domain.dto.MchOrderQueryDto;
import com.yeshimin.yeahboot.merchant.domain.dto.OrderShipDto;
import com.yeshimin.yeahboot.merchant.domain.dto.UpdateShipInfoDto;
import com.yeshimin.yeahboot.merchant.domain.vo.OrderDetailVo;
import com.yeshimin.yeahboot.merchant.service.MchOrderService;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.api.COrder;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.ApiInfoConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.contant.CompanyConstant;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.core.IBaseClient;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.PrintReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.request.corder.COrderReq;
import com.yeshimin.yeahboot.merchant.third.kuaidi100sdk.utils.SignUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 商家端订单
 */
@Slf4j
@RestController
@RequestMapping("/mch/order")
public class MchOrderController extends CrudController<OrderMapper, OrderEntity, OrderRepo> {

    @Autowired
    private MchOrderService service;

    @Autowired
    private Kuaidi100Properties kuaidi100Properties;

    public MchOrderController(OrderRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================

    /**
     * 订单物流-商家下单
     */
    @PublicAccess
//    @PostMapping("/delivery/submit")
    public R<Void> deliverySubmit() throws Exception {
        PrintReq printReq = new PrintReq();
        COrderReq cOrderReq = new COrderReq();
        cOrderReq.setKuaidicom(CompanyConstant.SF);
        cOrderReq.setSendManName("张三");
        cOrderReq.setSendManMobile("15966666666");
        cOrderReq.setSendManPrintAddr("西藏日喀则市定日县珠穆朗玛峰");
        cOrderReq.setRecManName("李四");
        cOrderReq.setRecManMobile("15966666666");
        cOrderReq.setRecManPrintAddr("西藏日喀则市定日县珠穆朗玛峰");
        cOrderReq.setCallBackUrl("http://106.12.84.37:18080/mch/order/delivery/notify");
        cOrderReq.setCargo("文件");
        cOrderReq.setRemark("测试数据，请忽略！");
        cOrderReq.setWeight("1");
        cOrderReq.setSalt("123456");

        String t = String.valueOf(System.currentTimeMillis());
        String param = new Gson().toJson(cOrderReq);

        String key = kuaidi100Properties.getKey();
        String secret = kuaidi100Properties.getSecret();

        printReq.setKey(key);
        printReq.setSign(SignUtils.printSign(param, t, key, secret));
        printReq.setT(t);
        printReq.setParam(param);
        printReq.setMethod(ApiInfoConstant.C_ORDER_METHOD);

        IBaseClient cOrder = new COrder();
        System.out.println(cOrder.execute(printReq));
        return R.ok();
    }

    /**
     * 订单物流 - 第三方回调通知（快递100）
     */
    @PublicAccess
//    @PostMapping(value = "/callback", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, Object> deliveryCallback(@RequestParam("taskId") String taskId,
                                                @RequestParam("sign") String sign,
                                                @RequestParam("param") String paramJson) {
        log.info("快递100回调 taskId={}, sign={}, param={}", taskId, sign, paramJson);

        try {
            // 1. 签名校验
            if (!verifySign(paramJson, sign)) {
                log.warn("快递100回调签名校验失败 taskId={}", taskId);
                return callbackFail("签名校验失败");
            }

            // 2. 解析 param
            Kd100CallbackParam param = JSON.parseObject(paramJson, Kd100CallbackParam.class);

            // 3. 幂等校验（示例：taskId + status）
            // if (alreadyProcessed(taskId, param.getData().getStatus())) {
            //     return callbackSuccess();
            // }

            // 4. 业务处理
            handleDeliveryStatus(param);

            // 5. 返回成功（一定要）
            return callbackSuccess();

        } catch (Exception e) {
            log.error("快递100回调处理异常 taskId={}", taskId, e);
            return callbackFail("服务器异常");
        }
    }

    /**
     * 查询店铺订单
     */
    @GetMapping("/query")
    public R<IPage<OrderEntity>> queryOrder(Page<OrderEntity> page, @Validated(Query.class) MchOrderQueryDto query) {
        Long userId = super.getUserId();
        IPage<OrderEntity> pageResult = service.queryOrder(userId, page, query);
        return R.ok(pageResult);
    }

    /**
     * 查询订单详情
     */
    @GetMapping("/detail")
    public R<OrderDetailVo> queryOrderDetail(@RequestParam("id") Long id) {
        Long userId = super.getUserId();
        OrderDetailVo result = service.queryOrderDetail(userId, id);
        return R.ok(result);
    }

    /**
     * 发货
     * 场景：手动发货后填写物流公司和快递单号
     */
    @PostMapping("/ship")
    public R<Void> shipOrder(@Validated @RequestBody OrderShipDto dto) {
        Long userId = super.getUserId();
        service.shipOrder(userId, dto);
        return R.ok();
    }

    /**
     * 更新发货信息
     */
    @PostMapping("/updateShipInfo")
    public R<Void> updateShipInfo(@Validated @RequestBody UpdateShipInfoDto dto) {
        Long userId = super.getUserId();
        service.updateShipInfo(userId, dto);
        return R.ok();
    }

    /**
     * 查询订单物流信息
     */
    @GetMapping("/queryTracking")
    public R<JSONObject> queryTracking(@RequestParam("orderId") Long orderId) {
        Long userId = super.getUserId();
        JSONObject result = service.queryTracking(userId, orderId);
        return R.ok(result);
    }

    // ================================================================================

    /**
     * 处理物流状态
     */
    private void handleDeliveryStatus(Kd100CallbackParam param) {
        Kd100CallbackParam.DataObj data = param.getData();
        Integer status = data.getStatus();
        String orderId = data.getOrderId();

        log.info("处理订单物流回调 orderId={}, status={}", orderId, status);

        switch (status) {
            case 0:
                // 下单成功
                break;
            case 1:
                // 已接单
                break;
            case 10:
                // 已取件
                break;
            case 101:
                // 运输中
                break;
            case 400:
                // 派送中
                break;
            case 13:
                // 已签收（⚠️ 可能触发订单完成逻辑）
                break;
            case 9:
            case 99:
                // 已取消
                break;
            default:
                log.info("未处理的物流状态 status={}", status);
        }
    }

    /**
     * 校验快递100签名
     * sign = MD5(param + salt).toUpperCase()
     */
    private boolean verifySign(String paramJson, String sign) {
        String callbackSalt = "123456"; // todo temp 下单时设置
        String localSign = DigestUtils
                .md5DigestAsHex((paramJson + callbackSalt).getBytes(StandardCharsets.UTF_8))
                .toUpperCase();
        return localSign.equals(sign);
    }

    /**
     * 回调成功返回
     */
    private Map<String, Object> callbackSuccess() {
        Map<String, Object> map = new HashMap<>();
        map.put("result", true);
        map.put("returnCode", "200");
        map.put("message", "成功");
        return map;
    }

    /**
     * 回调失败返回
     */
    private Map<String, Object> callbackFail(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", false);
        map.put("returnCode", "500");
        map.put("message", message);
        return map;
    }
}
