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

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminShopService {

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

        // 检查：编码是否已存在
        if (shopRepo.countByShopNo(dto.getShopNo()) > 0) {
            throw new BaseException("店铺编号已存在");
        }

        // 如果编号为空，则生成一个
        String shopNo = dto.getShopNo();
        if (StrUtil.isBlank(shopNo)) {
            shopNo = idService.nextEncodedId();
        }

        // 创建记录
        ShopEntity entity = BeanUtil.copyProperties(dto, ShopEntity.class);
        entity.setShopNo(shopNo);
        entity.insert();
        return entity;
    }

    /**
     * 查询
     */
    public IPage<ShopVo> query(Page<ShopEntity> page, ShopQueryDto query) {
        // 查询店铺
        LambdaQueryWrapper<ShopEntity> wrapper = QueryHelper.getQueryWrapper(query, ShopEntity.class);
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
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public ShopEntity update(ShopUpdateDto dto) {
        // 检查：数据是否存在
        ShopEntity entity = shopRepo.getOneById(dto.getId());

        BeanUtil.copyProperties(dto, entity);
        entity.updateById();
        return entity;
    }
}
