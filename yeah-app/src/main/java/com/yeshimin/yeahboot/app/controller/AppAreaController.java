package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.vo.AreaVo;
import com.yeshimin.yeahboot.app.service.AppAreaService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 地区相关
 */
@RestController
@RequestMapping("/app/area")
@RequiredArgsConstructor
public class AppAreaController extends BaseController {

    private final AppAreaService areaService;

    /**
     * 查询省份
     */
    @GetMapping("/province/query")
    public R<List<AreaVo>> queryProvince() {
        return R.ok(areaService.queryProvince());
    }

    /**
     * 查询城市
     */
    @GetMapping("/city/query")
    public R<List<AreaVo>> queryCity(@RequestParam("provinceCode") String provinceCode) {
        return R.ok(areaService.queryCity(provinceCode));
    }

    /**
     * 查询区县
     */
    @GetMapping("/district/query")
    public R<List<AreaVo>> queryDistrict(@RequestParam("cityCode") String cityCode) {
        return R.ok(areaService.queryDistrict(cityCode));
    }
}
