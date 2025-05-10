package com.yeshimin.yeahboot.upms.controller;

import com.yeshimin.yeahboot.upms.controller.base.CrudController;
import com.yeshimin.yeahboot.upms.domain.entity.AreaProvinceEntity;
import com.yeshimin.yeahboot.upms.mapper.AreaProvinceMapper;
import com.yeshimin.yeahboot.upms.repository.AreaProvinceRepo;
import com.yeshimin.yeahboot.upms.service.AreaProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 省份相关
 */
@RestController
@RequestMapping("/area/province")
public class AreaProvinceController extends CrudController<AreaProvinceMapper, AreaProvinceEntity, AreaProvinceRepo> {

    @Autowired
    private AreaProvinceService areaProvinceService;

    public AreaProvinceController(AreaProvinceRepo areaProvinceRepo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(areaProvinceRepo);
    }

    // ================================================================================
}
