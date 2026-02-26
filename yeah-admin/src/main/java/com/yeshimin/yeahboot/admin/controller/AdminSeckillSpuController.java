package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.service.AdminSeckillSpuService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSpuMapper;
import com.yeshimin.yeahboot.data.repository.SeckillSpuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀商品SPU表
 */
@RestController
@RequestMapping("/admin/seckillSpu")
public class AdminSeckillSpuController extends CrudController<SeckillSpuMapper, SeckillSpuEntity, SeckillSpuRepo> {

    @Autowired
    private AdminSeckillSpuService service;

    public AdminSeckillSpuController(SeckillSpuRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
