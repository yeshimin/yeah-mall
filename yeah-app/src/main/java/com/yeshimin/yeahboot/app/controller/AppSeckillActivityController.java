package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.SeckillActivityVo;
import com.yeshimin.yeahboot.app.service.AppSeckillActivityService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀活动
 */
@RestController
@RequestMapping("/app/seckillActivity")
@RequiredArgsConstructor
public class AppSeckillActivityController extends BaseController {

    private final AppSeckillActivityService service;

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<IPage<SeckillActivityVo>> query(Page<SeckillActivityEntity> page, SeckillActivityEntity query) {
        return R.ok(service.query(page, query));
    }
}
