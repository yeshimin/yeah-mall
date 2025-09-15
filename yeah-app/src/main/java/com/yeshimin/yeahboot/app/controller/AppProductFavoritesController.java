package com.yeshimin.yeahboot.app.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.FavoritesProductVo;
import com.yeshimin.yeahboot.app.domain.vo.FavoritesStatusVo;
import com.yeshimin.yeahboot.app.domain.vo.ResultVo;
import com.yeshimin.yeahboot.app.service.AppProductFavoritesService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductFavoritesEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商品收藏相关
 */
@RestController
@RequestMapping("/app/productFavorites")
@RequiredArgsConstructor
public class AppProductFavoritesController extends BaseController {

    private final AppProductFavoritesService service;

    /**
     * 商品-添加到收藏
     */
    @PostMapping("/add")
    public R<ResultVo> addToFavorites(@Valid @RequestBody IdDto dto) {
        Long userId = super.getUserId();
        boolean result = service.addToFavorites(userId, dto);
        return R.ok(new ResultVo(result));
    }

    /**
     * 商品-移除出收藏
     */
    @PostMapping("/remove")
    public R<ResultVo> removeFromFavorites(@Valid @RequestBody IdDto dto) {
        Long userId = super.getUserId();
        boolean result = service.removeFromFavorites(userId, dto);
        return R.ok(new ResultVo(result));
    }

    /**
     * 商品-查询收藏状态
     */
    @GetMapping("/status")
    public R<FavoritesStatusVo> queryFavoritesStatus(@RequestParam("spuId") Long spuId) {
        Long userId = super.getUserId();
        boolean result = service.queryFavoritesStatus(userId, spuId);
        return R.ok(new FavoritesStatusVo(result));
    }

    /**
     * 商品-查询收藏
     */
    @GetMapping("/query")
    public R<IPage<FavoritesProductVo>> queryFavorites(Page<ProductFavoritesEntity> page) {
        Long userId = super.getUserId();
        IPage<FavoritesProductVo> result = service.queryFavorites(userId, page);
        return R.ok(result);
    }
}
