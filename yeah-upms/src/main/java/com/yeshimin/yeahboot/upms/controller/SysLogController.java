package com.yeshimin.yeahboot.upms.controller;

import com.yeshimin.yeahboot.upms.controller.base.CrudController;
import com.yeshimin.yeahboot.upms.domain.entity.SysLogEntity;
import com.yeshimin.yeahboot.upms.mapper.SysLogMapper;
import com.yeshimin.yeahboot.upms.repository.SysLogRepo;
import com.yeshimin.yeahboot.upms.service.SysPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统日志相关
 */
@RestController
@RequestMapping("/sysLog")
public class SysLogController extends CrudController<SysLogMapper, SysLogEntity, SysLogRepo> {

    @Autowired
    private SysPostService sysPostService;

    public SysLogController(SysLogRepo sysPostRepo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(sysPostRepo);
    }

    // ================================================================================
}
