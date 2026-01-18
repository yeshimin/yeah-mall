package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.DeliveryProviderEntity;
import com.yeshimin.yeahboot.data.repository.DeliveryProviderRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.DeliveryProviderCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.DeliveryProviderUpdateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ShopDataIdDto;
import com.yeshimin.yeahboot.merchant.domain.dto.SyncExpCompanyDto;
import com.yeshimin.yeahboot.service.JuheExpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryProviderService {

    private final PermissionService permissionService;

    private final JuheExpService juheExpService;

    private final DeliveryProviderRepo deliveryProviderRepo;

    /**
     * 同步快递公司信息
     * 使用第三方接口
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncExpCompany(Long userId, SyncExpCompanyDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        JSONObject respObj = juheExpService.getExpressCompanyList();
        if (respObj == null) {
            throw new BaseException("同步快递公司信息失败");
        }

        if (!Objects.equals(respObj.getString("resultcode"), "200")) {
            throw new BaseException("同步快递公司信息失败，" + respObj.getString("reason"));
        }

        JSONArray companyList = respObj.getJSONArray("result");
        if (companyList == null || companyList.isEmpty()) {
            throw new BaseException("同步快递公司信息失败，未获取到公司列表");
        }
        log.info("同步快递公司信息，数量：{}", companyList.size());

        // 先清除当前数据
        deliveryProviderRepo.clearByShopId(dto.getShopId());

        List<DeliveryProviderEntity> listEntity = new ArrayList<>(companyList.size());
        for (int i = 0; i < companyList.size(); i++) {
            JSONObject companyObj = companyList.getJSONObject(i);
            String comName = companyObj.getString("com");
            String comCode = companyObj.getString("no");

            // 创建默认数据
            DeliveryProviderEntity entity = new DeliveryProviderEntity();
            entity.setMchId(dto.getMchId());
            entity.setShopId(dto.getShopId());
            entity.setName(comName);
            entity.setCode(comCode);
            entity.setIsDefault(false);
            listEntity.add(entity);
        }
        // save batch
        boolean r = deliveryProviderRepo.saveBatch(listEntity);
        log.info("同步快递公司信息，保存结果：{}", r);
    }

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(Long userId, DeliveryProviderCreateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：名称是否重复
        if (deliveryProviderRepo.countByShopIdAndName(dto.getShopId(), dto.getName()) > 0) {
            throw new BaseException("名称已存在");
        }
        // 检查：编码是否重复
        if (deliveryProviderRepo.countByShopIdAndCode(dto.getShopId(), dto.getCode()) > 0) {
            throw new BaseException("编码已存在");
        }

        DeliveryProviderEntity entity = BeanUtil.copyProperties(dto, DeliveryProviderEntity.class);
        boolean r = entity.insert();
        log.info("创建物流提供商，结果：{}", r);
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, DeliveryProviderUpdateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：是否存在
        DeliveryProviderEntity entity = deliveryProviderRepo.getOneById(dto.getId());
        // 检查：店铺数据权限
        permissionService.checkShopId(dto.getShopId(), entity.getShopId());

        // 按需检查：名称是否重复
        if (StrUtil.isNotBlank(dto.getName()) && !Objects.equals(dto.getName(), entity.getName())) {
            if (deliveryProviderRepo.countByShopIdAndName(dto.getShopId(), dto.getName()) > 0) {
                throw new BaseException("名称已存在");
            }
        }
        // 按需检查：编码是否重复
        if (StrUtil.isNotBlank(dto.getCode()) && !Objects.equals(dto.getCode(), entity.getCode())) {
            if (deliveryProviderRepo.countByShopIdAndCode(dto.getShopId(), dto.getCode()) > 0) {
                throw new BaseException("编码已存在");
            }
        }

        BeanUtil.copyProperties(dto, entity);
        boolean r = entity.updateById();
        log.info("更新物流提供商，结果：{}", r);
    }

    /**
     * 设置默认
     */
    @Transactional(rollbackFor = Exception.class)
    public void setDefault(Long userId, ShopDataIdDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：是否存在
        DeliveryProviderEntity entity = deliveryProviderRepo.getOneById(dto.getId());
        // 检查：店铺数据权限
        permissionService.checkShopId(dto.getShopId(), entity.getShopId());

        // 先取消所有默认
        deliveryProviderRepo.clearDefault(dto.getShopId());
        // 设置默认
        entity.setIsDefault(true);
        boolean r = entity.updateById();
        log.info("设置默认物流提供商，结果：{}", r);
    }
}
