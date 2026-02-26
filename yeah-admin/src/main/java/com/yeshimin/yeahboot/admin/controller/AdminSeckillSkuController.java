package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.service.AdminSeckillSkuService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSkuEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSkuMapper;
import com.yeshimin.yeahboot.data.repository.SeckillSkuRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀商品SKU表
 */
@RestController
@RequestMapping("/admin/seckillSku")
public class AdminSeckillSkuController extends CrudController<SeckillSkuMapper, SeckillSkuEntity, SeckillSkuRepo> {

    @Autowired
    private AdminSeckillSkuService service;

    public AdminSeckillSkuController(SeckillSkuRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
