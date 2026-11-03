package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.ShopCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.ShopQueryDto;
import com.yeshimin.yeahboot.admin.domain.dto.ShopUpdateDto;
import com.yeshimin.yeahboot.admin.domain.vo.ShopDetailVo;
import com.yeshimin.yeahboot.admin.domain.vo.ShopVo;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.IdService;
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

/**
 * 平台店铺管理服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminShopService {

    private static final String RESERVED_PLATFORM_SHOP_NO = "platform";
    private static final Pattern SHOP_NO_PATTERN = Pattern.compile("^[A-Za-z0-9_-]+$");

    private final ShopRepo shopRepo;
    private final MerchantRepo merchantRepo;

    private final IdService idService;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopEntity create(ShopCreateDto dto) {
        // 检查：商家是否存在
        if (merchantRepo.countById(dto.getMchId()) == 0) {
            throw new BaseException("商家未找到");
        }

        // 如果编号为空，则生成一个
        String shopNo = StrUtil.trim(dto.getShopNo());
        if (StrUtil.isBlank(shopNo)) {
            shopNo = idService.nextEncodedId();
        }
        if (RESERVED_PLATFORM_SHOP_NO.equalsIgnoreCase(shopNo)) {
            throw new BaseException("platform为系统保留店铺编号");
        }
        if (shopNo.length() > 32) {
            throw new BaseException("店铺编号长度不能超过32个字符");
        }
        if (!SHOP_NO_PATTERN.matcher(shopNo).matches()) {
            throw new BaseException("店铺编号仅支持数字、英文字母、中划线和下划线");
        }
        // 使用标准化后的编号查重，避免首尾空格绕过业务校验后再触发数据库唯一约束。
        if (shopRepo.countByShopNo(shopNo) > 0) {
            throw new BaseException("店铺编号已存在");
        }

        // 创建记录
        ShopEntity entity = BeanUtil.copyProperties(dto, ShopEntity.class);
        entity.setShopNo(shopNo);
        // 新店铺默认允许平台展示和交易，平台可在店铺管理中随时冻结。
        if (entity.getIsEnabled() == null) {
            entity.setIsEnabled(true);
        }
        if (entity.getSettlementCommissionRate() == null) {
            entity.setSettlementCommissionRate(BigDecimal.ZERO);
        }
        if (entity.getSettlementDelayDays() == null) {
            entity.setSettlementDelayDays(7);
        }
        entity.insert();
        return entity;
    }

    /**
     * 查询
     */
    public IPage<ShopVo> query(Page<ShopEntity> page, ShopQueryDto query, String keyword) {
        // 查询店铺
        LambdaQueryWrapper<ShopEntity> wrapper = QueryHelper.getQueryWrapper(query, ShopEntity.class);
        String normalizedKeyword = StrUtil.trim(keyword);
        if (StrUtil.isNotBlank(normalizedKeyword)) {
            wrapper.and(condition -> {
                condition.like(ShopEntity::getShopName, normalizedKeyword)
                        .or().like(ShopEntity::getShopNo, normalizedKeyword);
                try {
                    condition.or().eq(ShopEntity::getId, Long.parseLong(normalizedKeyword));
                } catch (NumberFormatException ignored) {
                    // 非数字关键词只匹配店铺名称和编号。
                }
            });
        }
        Page<ShopEntity> pageResult = shopRepo.page(page, wrapper);

        // 查询商家信息
        Set<Long> mchIds = pageResult.getRecords().stream().map(ShopEntity::getMchId).collect(Collectors.toSet());
        Map<Long, MerchantEntity> mapMch = merchantRepo.findListByIds(mchIds)
                .stream().collect(Collectors.toMap(MerchantEntity::getId, merchant -> merchant));

        return pageResult.convert(e -> {
            ShopVo vo = new ShopVo();
            BeanUtil.copyProperties(e, vo);
            vo.setMchName(mapMch.get(e.getMchId()).getLoginAccount());
            return vo;
        });
    }


    /**
     * 详情
     */
    public ShopDetailVo detail(Long id) {
        // 查询店铺
        ShopEntity entity = shopRepo.getOneById(id);

        // 查询商家
        MerchantEntity merchant = merchantRepo.getOneById(entity.getMchId());

        ShopDetailVo vo = BeanUtil.copyProperties(entity, ShopDetailVo.class);
        vo.setMchName(merchant.getLoginAccount());
        return vo;
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopEntity update(ShopUpdateDto dto) {
        // 检查：数据是否存在
        ShopEntity entity = shopRepo.getOneById(dto.getId());

        BeanUtil.copyProperties(dto, entity);
        entity.updateById();
        return entity;
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Collection<Long> ids) {
        for (Long id : ids) {
            // 检查：数据是否存在
            ShopEntity entity = shopRepo.getOneById(id);

            // 仅允许删除从未产生任何关联业务数据的空店铺。
            if (shopRepo.hasBusinessReferences(id)) {
                throw new BaseException("该店铺存在关联业务数据，不可删除，请改为停用");
            }
        }

        shopRepo.removeByIds(ids);
    }
}
