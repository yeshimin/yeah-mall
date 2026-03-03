package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.vo.AdminSeckillSpuDetailVo;
import com.yeshimin.yeahboot.admin.service.AdminSeckillSpuService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀商品SPU表
 */
@RestController
@RequestMapping("/admin/seckillSpu")
@RequiredArgsConstructor
public class AdminSeckillSpuController extends BaseController {

    private final AdminSeckillSpuService service;

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<IPage<SeckillSpuVo>> query(Page<SeckillSpuEntity> page, @Validated SeckillSpuQueryDto query) {
        return R.ok(service.query(page, query));
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<AdminSeckillSpuDetailVo> detail(@RequestParam Long id) {
        return R.ok(service.detail(id));
    }
}
