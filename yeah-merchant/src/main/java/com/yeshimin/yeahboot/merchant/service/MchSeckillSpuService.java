package com.yeshimin.yeahboot.merchant.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuImageEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import com.yeshimin.yeahboot.data.repository.SeckillSkuRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSpuImageRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSpuRepo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchSeckillSkuVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchSeckillSpuDetailVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchSeckillSpuVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MchSeckillSpuService {

    private final SeckillSpuRepo seckillSpuRepo;
    private final SeckillSpuImageRepo seckillSpuImageRepo;
    private final SeckillSkuRepo seckillSkuRepo;

    private final PermissionService permissionService;

    /**
     * 查询报名列表
     */
    public IPage<SeckillSpuVo> query(Page<SeckillSpuEntity> page, SeckillSpuQueryDto query, Long userId) {
        return seckillSpuRepo.query(page, query, userId);
    }

    /**
     * 详情
     */
    public MchSeckillSpuDetailVo detail(Long userId, Long id) {
        // 检查：秒杀spu是否存在
        SeckillSpuEntity seckillSpu = seckillSpuRepo.getOneById(id, "秒杀spu不存在");
        // 权限检查和控制
        permissionService.checkMch(userId, seckillSpu);

        // 查询spu对应图片集合
        List<String> spuImages = seckillSpuImageRepo.findListBySeckillSpuId(seckillSpu.getId())
                .stream().map(SeckillSpuImageEntity::getImageUrl).collect(Collectors.toList());

        // 查询秒杀sku信息
        List<SeckillSkuEntity> skuList = seckillSkuRepo.findListBySeckillSpuId(seckillSpu.getId());

        MchSeckillSpuDetailVo vo = new MchSeckillSpuDetailVo();
        vo.setSpu(BeanUtil.copyProperties(seckillSpu, MchSeckillSpuVo.class));
        vo.setImages(spuImages);
        vo.setSkuList(BeanUtil.copyToList(skuList, MchSeckillSkuVo.class));
        return vo;
    }
}
