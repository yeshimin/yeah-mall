package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MchSeckillSpuDetailVo;
import com.yeshimin.yeahboot.merchant.service.MchSeckillSpuService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家端-秒杀商品SPU表
 */
@RestController
@RequestMapping("/mch/seckillSpu")
@RequiredArgsConstructor
public class MchSeckillSpuController extends BaseController {

    private final MchSeckillSpuService service;

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<IPage<SeckillSpuVo>> query(Page<SeckillSpuEntity> page, @Validated SeckillSpuQueryDto query) {
        Long userId = super.getUserId();
        return R.ok(service.query(page, query, userId));
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<MchSeckillSpuDetailVo> detail(@RequestParam Long id) {
        Long userId = super.getUserId();
        return R.ok(service.detail(userId, id));
    }
}
