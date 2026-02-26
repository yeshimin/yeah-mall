package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.service.AdminSeckillSpuImageService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuImageEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSpuImageMapper;
import com.yeshimin.yeahboot.data.repository.SeckillSpuImageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀商品SPU图片表
 */
@RestController
@RequestMapping("/admin/seckillSpuImage")
public class AdminSeckillSpuImageController extends CrudController<SeckillSpuImageMapper, SeckillSpuImageEntity, SeckillSpuImageRepo> {

    @Autowired
    private AdminSeckillSpuImageService service;

    public AdminSeckillSpuImageController(SeckillSpuImageRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
