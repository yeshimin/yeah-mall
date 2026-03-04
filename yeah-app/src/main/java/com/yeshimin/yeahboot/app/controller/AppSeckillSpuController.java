package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.service.AppSeckillSpuService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀商品SPU
 */
@RestController
@RequestMapping("/app/seckillSpu")
@RequiredArgsConstructor
public class AppSeckillSpuController extends BaseController {

    private final AppSeckillSpuService service;

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<IPage<SeckillSpuVo>> query(Page<SeckillSpuEntity> page, @Validated SeckillSpuQueryDto query) {
        return R.ok(service.query(page, query));
    }
}
