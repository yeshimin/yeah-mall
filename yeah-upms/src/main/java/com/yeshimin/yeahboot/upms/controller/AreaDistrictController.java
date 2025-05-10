package com.yeshimin.yeahboot.upms.controller;

import com.yeshimin.yeahboot.upms.controller.base.CrudController;
import com.yeshimin.yeahboot.upms.domain.entity.AreaDistrictEntity;
import com.yeshimin.yeahboot.upms.mapper.AreaDistrictMapper;
import com.yeshimin.yeahboot.upms.repository.AreaDistrictRepo;
import com.yeshimin.yeahboot.upms.service.AreaDistrictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 区县相关
 */
@RestController
@RequestMapping("/area/district")
public class AreaDistrictController extends CrudController<AreaDistrictMapper, AreaDistrictEntity, AreaDistrictRepo> {

    @Autowired
    private AreaDistrictService areaDistrictService;

    public AreaDistrictController(AreaDistrictRepo areaDistrictRepo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(areaDistrictRepo);
    }

    // ================================================================================
}
