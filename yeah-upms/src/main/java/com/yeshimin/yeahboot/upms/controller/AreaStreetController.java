package com.yeshimin.yeahboot.upms.controller;

import com.yeshimin.yeahboot.upms.controller.base.CrudController;
import com.yeshimin.yeahboot.upms.domain.entity.AreaStreetEntity;
import com.yeshimin.yeahboot.upms.mapper.AreaStreetMapper;
import com.yeshimin.yeahboot.upms.repository.AreaStreetRepo;
import com.yeshimin.yeahboot.upms.service.AreaStreetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 街道相关
 */
@RestController
@RequestMapping("/area/street")
public class AreaStreetController extends CrudController<AreaStreetMapper, AreaStreetEntity, AreaStreetRepo> {

    @Autowired
    private AreaStreetService areaStreetService;

    public AreaStreetController(AreaStreetRepo areaStreetRepo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(areaStreetRepo);
    }

    // ================================================================================
}
