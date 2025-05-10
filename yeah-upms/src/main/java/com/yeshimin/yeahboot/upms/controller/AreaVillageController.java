package com.yeshimin.yeahboot.upms.controller;

import com.yeshimin.yeahboot.upms.controller.base.CrudController;
import com.yeshimin.yeahboot.upms.domain.entity.AreaVillageEntity;
import com.yeshimin.yeahboot.upms.mapper.AreaVillageMapper;
import com.yeshimin.yeahboot.upms.repository.AreaVillageRepo;
import com.yeshimin.yeahboot.upms.service.AreaVillageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 乡村相关
 */
@RestController
@RequestMapping("/area/village")
public class AreaVillageController extends CrudController<AreaVillageMapper, AreaVillageEntity, AreaVillageRepo> {

    @Autowired
    private AreaVillageService areaVillageService;

    public AreaVillageController(AreaVillageRepo areaVillageRepo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(areaVillageRepo);
    }

    // ================================================================================
}
