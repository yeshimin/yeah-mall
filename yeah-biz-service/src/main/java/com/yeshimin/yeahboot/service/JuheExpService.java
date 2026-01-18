package com.yeshimin.yeahboot.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yeshimin.yeahboot.common.properties.JuheApiProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 聚合数据平台-快递服务
 * https://www.juhe.cn/docs/api/id/43
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JuheExpService {

    private final JuheApiProperties juheApiProperties;

    /**
     * 查询快递信息
     *
     * @param com           快递公司编号
     * @param no            快递单号
     * @param senderPhone   寄件人手机号后四位（可选）
     * @param receiverPhone 收件人手机号后四位（可选）
     * @return 查询结果
     */
    public JSONObject queryExpress(String com, String no, String senderPhone, String receiverPhone) {
        try {
            // 设置请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("key", juheApiProperties.getKey());
            params.put("com", com);
            params.put("no", no);
            if (StrUtil.isNotBlank(senderPhone)) {
                params.put("senderPhone", senderPhone);
            }
            if (StrUtil.isNotBlank(receiverPhone)) {
                params.put("receiverPhone", receiverPhone);
            }
            params.put("dtype", "json");

            // 发送请求
            log.info("查询快递信息，com: {}, no: {}, senderPhone: {}, receiverPhone: {}",
                    com, no, senderPhone, receiverPhone);
            String response = HttpUtil.post(juheApiProperties.getQueryExpUrl(), params);
            log.info("查询快递信息：{}", response);

            // 解析响应
            return JSON.parseObject(response);
        } catch (Exception e) {
            log.error("查询快递信息失败", e);
            throw new RuntimeException("查询快递信息失败", e);
        }
    }

    /**
     * 查询快递公司编号对照表
     *
     * @return 快递公司列表
     */
    public JSONObject getExpressCompanyList() {
        try {
            // 设置请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("key", juheApiProperties.getKey());

            // 发送请求
            String response = HttpUtil.post(juheApiProperties.getQueryComUrl(), params);
            log.info("查询快递公司编号对照表响应：{}", response);

            // 解析响应
            return JSON.parseObject(response);
        } catch (Exception e) {
            log.error("查询快递公司编号对照表失败", e);
            throw new RuntimeException("查询快递公司编号对照表失败", e);
        }
    }
}
