package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.vo.AdminSeckillSkuVo;
import com.yeshimin.yeahboot.admin.domain.vo.AdminSeckillSpuDetailVo;
import com.yeshimin.yeahboot.admin.domain.vo.AdminSeckillSpuVo;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuImageEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import com.yeshimin.yeahboot.data.repository.SeckillSkuRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSpuImageRepo;
import com.yeshimin.yeahboot.data.repository.SeckillSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSeckillSpuService {

    private final SeckillSpuRepo seckillSpuRepo;
    private final SeckillSpuImageRepo seckillSpuImageRepo;
    private final SeckillSkuRepo seckillSkuRepo;

    /**
     * 查询报名列表
     */
    public IPage<SeckillSpuVo> query(Page<SeckillSpuEntity> page, SeckillSpuQueryDto query) {
        return seckillSpuRepo.query(page, query, null);
    }

    /**
     * 详情
     */
    public AdminSeckillSpuDetailVo detail(Long id) {
        // 检查：秒杀spu是否存在
        SeckillSpuEntity seckillSpu = seckillSpuRepo.getOneById(id, "秒杀spu不存在");

        // 查询spu对应图片集合
        List<String> spuImages = seckillSpuImageRepo.findListBySeckillSpuId(seckillSpu.getId())
                .stream().map(SeckillSpuImageEntity::getImageUrl).collect(Collectors.toList());

        // 查询秒杀sku信息
        List<SeckillSkuEntity> skuList = seckillSkuRepo.findListBySeckillSpuId(seckillSpu.getId());

        AdminSeckillSpuDetailVo vo = new AdminSeckillSpuDetailVo();
        vo.setSpu(BeanUtil.copyProperties(seckillSpu, AdminSeckillSpuVo.class));
        vo.setImages(spuImages);
        vo.setSkuList(BeanUtil.copyToList(skuList, AdminSeckillSkuVo.class));
        return vo;
    }
}
