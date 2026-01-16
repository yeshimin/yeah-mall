package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.BooleanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yeshimin.yeahboot.app.domain.dto.MemberAddressCreateDto;
import com.yeshimin.yeahboot.app.domain.dto.MemberAddressQueryDto;
import com.yeshimin.yeahboot.app.domain.dto.MemberAddressUpdateDto;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.data.domain.entity.MemberAddressEntity;
import com.yeshimin.yeahboot.data.repository.MemberAddressRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppMemberAddressService {

    private final MemberAddressRepo memberAddressRepo;

    private final AppAreaService appAreaService;

    /**
     * 买家端-收货地址最大数量
     */
    @Value("${member-address-max-count:20}")
    private Integer memberAddressMaxCount;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(Long userId, MemberAddressCreateDto dto) {
        // 检查：最大收货地址数量
        if (memberAddressRepo.countByMemberId(userId) >= memberAddressMaxCount) {
            throw new RuntimeException("收货地址数量已达上限，无法新增");
        }
        // 检查省市区编码和名称
        if (!appAreaService.check(dto.getProvinceCode(), dto.getProvinceName(),
                dto.getCityCode(), dto.getCityName(), dto.getDistrictCode(), dto.getDistrictName())) {
            throw new RuntimeException("省市区编码或名称不正确");
        }

        // 如果新增的设置为默认地址，则把其他地址的默认状态去掉
        if (BooleanUtil.isTrue(dto.getIsDefault())) {
            memberAddressRepo.clearDefault(userId);
        }

        MemberAddressEntity entity = BeanUtil.copyProperties(dto, MemberAddressEntity.class);
        entity.setMemberId(userId);
        // 完整地址
        String fullAddress =
                entity.getProvinceName() + entity.getCityName() + entity.getDistrictName() + entity.getDetailAddress();
        entity.setFullAddress(fullAddress);
        boolean r = entity.insert();
        log.info("AppMemberAddressService.create.result: {}", r);
    }

    /**
     * 查询
     */
    public List<MemberAddressEntity> query(Long userId, MemberAddressQueryDto query) {
        LambdaQueryWrapper<MemberAddressEntity> wrapper = QueryHelper.getQueryWrapper(query);
        wrapper.eq(MemberAddressEntity::getMemberId, userId);
        // 排序：优先默认地址，其次按更新时间降序
        wrapper.orderByDesc(MemberAddressEntity::getIsDefault)
                .orderByDesc(MemberAddressEntity::getUpdateTime);
        return memberAddressRepo.list(wrapper);
    }

    /**
     * 详情
     */
    public MemberAddressEntity detail(Long userId, Long id) {
        // 检查：收货地址是否存在；权限是否正确
        MemberAddressEntity entity = memberAddressRepo.findOneById(id);
        if (entity == null || !Objects.equals(entity.getMemberId(), userId)) {
            throw new RuntimeException("收货地址不存在");
        }
        return entity;
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, MemberAddressUpdateDto dto) {
        // 检查：收货地址是否存在；权限是否正确
        MemberAddressEntity entity = memberAddressRepo.findOneById(dto.getId());
        if (entity == null || !Objects.equals(entity.getMemberId(), userId)) {
            throw new RuntimeException("收货地址不存在");
        }
        // 检查省市区编码和名称
        if (!appAreaService.check(dto.getProvinceCode(), dto.getProvinceName(),
                dto.getCityCode(), dto.getCityName(), dto.getDistrictCode(), dto.getDistrictName())) {
            throw new RuntimeException("省市区编码或名称不正确");
        }

        // 如果更新的设置为默认地址，则把其他地址的默认状态去掉
        if (BooleanUtil.isTrue(dto.getIsDefault())) {
            memberAddressRepo.clearDefault(userId);
        }

        BeanUtil.copyProperties(dto, entity);
        // 完整地址
        String fullAddress =
                entity.getProvinceName() + entity.getCityName() + entity.getDistrictName() + entity.getDetailAddress();
        entity.setFullAddress(fullAddress);
        boolean r = entity.updateById();
        log.info("AppMemberAddressService.update.result: {}", r);
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long userId, IdDto dto) {
        // 检查：收货地址是否存在；权限是否正确
        MemberAddressEntity entity = memberAddressRepo.findOneById(dto.getId());
        if (entity == null || !Objects.equals(entity.getMemberId(), userId)) {
            throw new RuntimeException("收货地址不存在");
        }

        boolean r = entity.deleteById();
        log.info("AppMemberAddressService.delete.result: {}", r);
    }
}
