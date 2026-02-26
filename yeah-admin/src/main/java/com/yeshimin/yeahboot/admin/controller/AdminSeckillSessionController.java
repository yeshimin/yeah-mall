package com.yeshimin.yeahboot.admin.controller;

import com.yeshimin.yeahboot.admin.service.AdminSeckillSessionService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSessionEntity;
import com.yeshimin.yeahboot.data.mapper.SeckillSessionMapper;
import com.yeshimin.yeahboot.data.repository.SeckillSessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 秒杀活动场次
 */
@RestController
@RequestMapping("/admin/seckillSession")
public class AdminSeckillSessionController extends CrudController<SeckillSessionMapper, SeckillSessionEntity, SeckillSessionRepo> {

    @Autowired
    private AdminSeckillSessionService service;

    public AdminSeckillSessionController(SeckillSessionRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("admin:seckillSession");
    }

    // ================================================================================
}
