package com.yeshimin.yeahboot.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.SeckillApplyAuditDto;
import com.yeshimin.yeahboot.admin.domain.vo.SeckillActivityApplyDetailVo;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityApplyAuditStatusEnum;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSeckillActivityApplyService {

    private final SeckillSpuRepo seckillSpuRepo;
    private final SeckillSpuImageRepo seckillSpuImageRepo;
    private final SeckillActivityApplyRepo seckillActivityApplyRepo;
    private final SeckillSkuRepo seckillSkuRepo;

    /**
     * 查询报名列表
     */
    public IPage<SeckillActivityApplyVo> query(Page<SeckillActivityApplyEntity> page,
                                               SeckillActivityApplyQueryDto query) {
        return seckillActivityApplyRepo.query(page, query, null);
    }

    /**
     * 详情
     */
    public SeckillActivityApplyDetailVo detail(Long id) {
        // 检查：申请记录是否为空
        SeckillActivityApplyEntity apply = seckillActivityApplyRepo.getOneById(id, "申请记录不存在");

        // 查询秒杀spu信息
        SeckillSpuEntity seckillSpu = seckillSpuRepo.getOneById(apply.getSeckillSpuId());
        // 查询spu对应图片集合
        List<String> spuImages = seckillSpuImageRepo.findListBySeckillSpuId(apply.getSeckillSpuId())
                .stream().map(SeckillSpuImageEntity::getImageUrl).collect(Collectors.toList());

        // 查询秒杀sku信息
        List<SeckillSkuEntity> skuList = seckillSkuRepo.findListBySeckillSpuId(apply.getSeckillSpuId());

        SeckillActivityApplyDetailVo vo = new SeckillActivityApplyDetailVo();
        vo.setApply(apply);
        vo.setSpu(seckillSpu);
        vo.setImages(spuImages);
        vo.setSkuList(skuList);
        return vo;
    }

    /**
     * 审核报名
     */
    @Transactional(rollbackFor = Exception.class)
    public void audit(SeckillApplyAuditDto dto) {
        // 检查：申请记录是否存在
        SeckillActivityApplyEntity entity = seckillActivityApplyRepo.getOneById(dto.getApplyId(), "申请记录不存在");
        // 检查：仅当状态为待审核时才可审核
        if (!Objects.equals(entity.getAuditStatus(), SeckillActivityApplyAuditStatusEnum.PENDING.getIntValue())) {
            throw new IllegalStateException("仅当状态为待审核时才可审核");
        }
        SeckillSpuEntity seckillSpu = seckillSpuRepo.getOneById(entity.getSeckillSpuId());

        // 更新申请记录审核结果
        entity.setAuditStatus(dto.getAuditStatus());
        entity.setAuditRemark(dto.getAuditRemark());
        entity.updateById();

        // 更新秒杀spu记录审核结果
        seckillSpu.setAuditStatus(dto.getAuditStatus());
        seckillSpu.updateById();
    }
}
