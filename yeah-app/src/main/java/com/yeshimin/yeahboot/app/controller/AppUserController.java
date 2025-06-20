package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.entity.AppUserEntity;
import com.yeshimin.yeahboot.app.mapper.AppUserMapper;
import com.yeshimin.yeahboot.app.repository.AppUserRepo;
import com.yeshimin.yeahboot.app.service.AppUserService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * app端用户表
 */
@Slf4j
@RestController
@RequestMapping("/app/appUser")
public class AppUserController extends CrudController<AppUserMapper, AppUserEntity, AppUserRepo> {

    @Autowired
    private AppUserService service;

    public AppUserController(AppUserRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
    }

    // ================================================================================
}
