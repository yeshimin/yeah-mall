package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.common.enums.CouponTypeEnum;
import com.yeshimin.yeahboot.data.common.enums.CouponUseRangeEnum;
import com.yeshimin.yeahboot.data.domain.entity.MchCouponEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.repository.MchCouponRepo;
import com.yeshimin.yeahboot.data.repository.ProductCategoryRepo;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.MchCouponCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.MchCouponUpdateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.MchCouponUpdateStatusDto;
import com.yeshimin.yeahboot.merchant.domain.vo.MchCouponDetailVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MchCouponService {

    private final PermissionService permissionService;

    private final ProductSpuRepo productSpuRepo;
    private final ProductCategoryRepo productCategoryRepo;
    private final MchCouponRepo mchCouponRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(Long userId, MchCouponCreateDto dto) {
        // 检查权限和参数
        this.checkPermAndParams(userId, dto);

        // 保存记录
        MchCouponEntity entity = BeanUtil.copyProperties(dto, MchCouponEntity.class);
        boolean r = entity.insert();
        log.info("创建优惠券结果：{}", r);
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(Long userId, MchCouponUpdateDto dto) {
        // 检查权限和参数
        this.checkPermAndParams(userId, dto);
        // 检查：数据是否存在
        MchCouponEntity old = mchCouponRepo.getOneById(dto.getId());
        // 检查所属是否正确
        if (!Objects.equals(dto.getMchId(), old.getMchId()) || !Objects.equals(dto.getShopId(), old.getShopId())) {
            throw new BaseException("数据所属不正确");
        }

        // 保存记录
        MchCouponEntity entity = BeanUtil.copyProperties(dto, MchCouponEntity.class);
        boolean r = entity.updateById();
        log.info("更新优惠券结果：{}", r);
    }


    /**
     * 更新状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Long userId, MchCouponUpdateStatusDto dto) {
        // 检查：数据是否存在
        MchCouponEntity old = mchCouponRepo.getOneById(dto.getId());
        // 检查权限和控制
        permissionService.checkMchAndShop(userId, old);

        // 更新
        MchCouponEntity entity = BeanUtil.copyProperties(dto, MchCouponEntity.class);
        boolean r = entity.updateById();
        log.info("更新优惠券状态结果：{}", r);
    }

    /**
     * 详情
     */
    public MchCouponDetailVo detail(Long userId, Long id) {
        // TODO
        return null;
    }

    // ================================================================================

    /**
     * 检查权限和参数
     */
    private void checkPermAndParams(Long userId, MchCouponCreateDto dto) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 检查：优惠券类型
        CouponTypeEnum couponType = CouponTypeEnum.of(dto.getType());
        // 满减券
        if (couponType == CouponTypeEnum.FULL_REDUCTION) {
            // 检查：优惠金额
            if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaseException("满减券优惠金额必须大于0");
            }
            // 检查：使用门槛金额
            if (dto.getMinAmount() == null || dto.getMinAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaseException("满减券使用门槛金额必须大于0");
            }
            // 检查：使用门槛金额必须大于优惠金额
            if (dto.getMinAmount().compareTo(dto.getAmount()) < 0) {
                throw new BaseException("满减券使用门槛金额必须大于优惠金额");
            }
            // 折扣置空
            dto.setDiscount(null);
        } else if (couponType == CouponTypeEnum.DISCOUNT) {
            // 检查：优惠折扣 [0.01, 0.99]
            if (dto.getDiscount() == null || dto.getDiscount().compareTo(new BigDecimal("0.01")) < 0
                    || dto.getDiscount().compareTo(new BigDecimal("0.99")) > 0) {
                throw new BaseException("折扣券优惠折扣必须在0.01和0.99之间");
            }
            // 检查：使用门槛金额
            if (dto.getMinAmount() == null || dto.getMinAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaseException("折扣券使用门槛金额必须大于0");
            }
            // 优惠金额置空
            dto.setAmount(null);
        } else if (couponType == CouponTypeEnum.NO_THRESHOLD) {
            // 检查：优惠金额
            if (dto.getAmount() == null || dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new BaseException("无门槛券优惠金额必须大于0");
            }
            // 优惠折扣置空
            dto.setDiscount(null);
            // 使用门槛金额设置为0
            dto.setMinAmount(BigDecimal.ZERO);
        } else {
            throw new BaseException("优惠券类型错误");
        }
        // 检查：使用范围
        CouponUseRangeEnum useRange = CouponUseRangeEnum.of(dto.getUseRange());
        if (useRange == CouponUseRangeEnum.SHOP) {
            // 直接设置targetId为当前店铺ID
            dto.setTargetId(dto.getShopId());
        } else if (useRange == CouponUseRangeEnum.PRODUCT) {
            // 检查：目标商品是否属于当前店铺
            ProductSpuEntity spu = productSpuRepo.getOneById(dto.getTargetId());
            if (!Objects.equals(spu.getShopId(), dto.getShopId())) {
                throw new BaseException("指定商品优惠券目标ID错误");
            }
        } else if (useRange == CouponUseRangeEnum.CATEGORY) {
            // 检查：目标分类是否属于当前店铺
            ProductCategoryEntity category = productCategoryRepo.getOneById(dto.getTargetId());
            if (!Objects.equals(category.getShopId(), dto.getShopId())) {
                throw new BaseException("指定分类优惠券目标ID错误");
            }
        } else {
            throw new BaseException("优惠券使用范围错误");
        }
        // 检查：有效期时间范围
        if (!YsmUtils.checkDateTime(dto.getBeginTime(), dto.getEndTime())) {
            throw new BaseException("优惠券有效期时间范围错误");
        }
    }
}
