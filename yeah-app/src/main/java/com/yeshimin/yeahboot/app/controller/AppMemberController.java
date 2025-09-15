package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.vo.FavoritesStatusVo;
import com.yeshimin.yeahboot.app.domain.vo.ResultVo;
import com.yeshimin.yeahboot.app.service.AppMemberService;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 会员表
 */
@RestController
@RequestMapping("/app/member")
@RequiredArgsConstructor
public class AppMemberController extends BaseController {

    private final AppMemberService service;

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<MemberEntity> detail() {
        Long userId = WebContextUtils.getUserId();
        return R.ok(service.detail(userId));
    }

    /**
     * 商品-添加到收藏夹
     */
    @PostMapping("/product/addToFavorites")
    public R<ResultVo> addToFavorites(@Valid @RequestBody IdDto dto) {
        Long userId = WebContextUtils.getUserId();
        boolean result = service.addToFavorites(userId, dto);
        return R.ok(new ResultVo(result));
    }

    /**
     * 商品-移除出收藏夹
     */
    @PostMapping("/product/removeFromFavorites")
    public R<ResultVo> removeFromFavorites(@Valid @RequestBody IdDto dto) {
        Long userId = WebContextUtils.getUserId();
        boolean result = service.removeFromFavorites(userId, dto);
        return R.ok(new ResultVo(result));
    }

    /**
     * 商品-查询收藏状态
     */
    @GetMapping("/product/favorites/status")
    public R<FavoritesStatusVo> queryFavoritesStatus(@RequestParam("spuId") Long spuId) {
        Long userId = WebContextUtils.getUserId();
        boolean result = service.queryFavoritesStatus(userId, spuId);
        return R.ok(new FavoritesStatusVo(result));
    }
}
