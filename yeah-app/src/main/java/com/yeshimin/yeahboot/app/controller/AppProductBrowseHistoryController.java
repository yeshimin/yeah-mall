package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.BrowseHistoryProductVo;
import com.yeshimin.yeahboot.app.service.AppProductBrowseHistoryService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductBrowseHistoryEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * App商品浏览足迹接口
 */
@RestController
@RequestMapping("/app/productBrowseHistory")
@RequiredArgsConstructor
public class AppProductBrowseHistoryController extends BaseController {

    private final AppProductBrowseHistoryService service;

    /**
     * 查询浏览足迹。
     */
    @GetMapping("/query")
    public R<IPage<BrowseHistoryProductVo>> query(Page<ProductBrowseHistoryEntity> page) {
        return R.ok(service.query(super.getUserId(), page));
    }

    /**
     * 删除单条浏览足迹。
     */
    @PostMapping("/remove")
    public R<Void> remove(@Valid @RequestBody IdDto dto) {
        service.remove(super.getUserId(), dto);
        return R.ok();
    }

    /**
     * 清空浏览足迹。
     */
    @PostMapping("/clear")
    public R<Void> clear() {
        service.clear(super.getUserId());
        return R.ok();
    }
}
