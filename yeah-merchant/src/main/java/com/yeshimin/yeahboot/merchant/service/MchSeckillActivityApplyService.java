package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.SeckillActivityApplyQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityApplyEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuImageEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillActivityApplyVo;
import com.yeshimin.yeahboot.data.repository.SeckillActivityApplyRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSkuRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSpuImageRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSpuRepo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchSeckillActivityApplyDetailVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchSeckillActivityApplyVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchSeckillSkuVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchSeckillSpuVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MchSeckillActivityApplyService {

    private final SeckillSpuRepo seckillSpuRepo;
    private final SeckillSpuImageRepo seckillSpuImageRepo;
    private final SeckillActivityApplyRepo seckillActivityApplyRepo;
    private final SeckillSkuRepo seckillSkuRepo;

    private final PermissionService permissionService;

    /**
     * 查询报名列表
     */
    public IPage<SeckillActivityApplyVo> query(Page<SeckillActivityApplyEntity> page,
                                               SeckillActivityApplyQueryDto query, Long userId) {
        return seckillActivityApplyRepo.query(page, query, userId);
    }

    /**
     * 详情
     */
    public MchSeckillActivityApplyDetailVo detail(Long userId, Long id) {
        // 检查：申请记录是否为空
        SeckillActivityApplyEntity apply = seckillActivityApplyRepo.getOneById(id, "申请记录不存在");
        // 权限检查和控制
        permissionService.checkMch(userId, apply);

        // 查询秒杀spu信息
        SeckillSpuEntity seckillSpu = seckillSpuRepo.getOneById(apply.getSeckillSpuId());
        // 查询spu对应图片集合
        List<String> spuImages = seckillSpuImageRepo.findListBySeckillSpuId(apply.getSeckillSpuId())
                .stream().map(SeckillSpuImageEntity::getImageUrl).collect(Collectors.toList());

        // 查询秒杀sku信息
        List<SeckillSkuEntity> skuList = seckillSkuRepo.findListBySeckillSpuId(apply.getSeckillSpuId());

        MchSeckillActivityApplyDetailVo vo = new MchSeckillActivityApplyDetailVo();
        vo.setApply(BeanUtil.copyProperties(apply, MchSeckillActivityApplyVo.class));
        vo.setSpu(BeanUtil.copyProperties(seckillSpu, MchSeckillSpuVo.class));
        vo.setImages(spuImages);
        vo.setSkuList(BeanUtil.copyToList(skuList, MchSeckillSkuVo.class));
        return vo;
    }
}
