package com.yeshimin.yeahboot.basic.controller;

import com.yeshimin.yeahboot.basic.domain.entity.AreaCityEntity;
import com.yeshimin.yeahboot.basic.mapper.AreaCityMapper;
import com.yeshimin.yeahboot.basic.repository.AreaCityRepo;
import com.yeshimin.yeahboot.basic.service.AreaCityService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
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
