package com.yeshimin.yeahboot.upms.controller;

import com.yeshimin.yeahboot.upms.controller.base.CrudController;
import com.yeshimin.yeahboot.upms.domain.entity.AreaCityEntity;
import com.yeshimin.yeahboot.upms.mapper.AreaCityMapper;
import com.yeshimin.yeahboot.upms.repository.AreaCityRepo;
import com.yeshimin.yeahboot.upms.service.AreaCityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 城市相关
 */
@RestController
@RequestMapping("/area/city")
public class AreaCityController extends CrudController<AreaCityMapper, AreaCityEntity, AreaCityRepo> {

    @Autowired
    private AreaCityService areaCityService;

    public AreaCityController(AreaCityRepo areaCityRepo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(areaCityRepo);
    }

    // ================================================================================
}
