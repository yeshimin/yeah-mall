package com.yeshimin.yeahboot.app.controller;

import com.yeshimin.yeahboot.app.domain.dto.MemberAddressCreateDto;
import com.yeshimin.yeahboot.app.domain.dto.MemberAddressQueryDto;
import com.yeshimin.yeahboot.app.domain.dto.MemberAddressUpdateDto;
import com.yeshimin.yeahboot.app.service.AppMemberAddressService;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.IdDto;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MemberAddressEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 会员收货地址
 */
@RestController
@RequestMapping("/app/memberAddress")
@RequiredArgsConstructor
public class AppMemberAddressController extends BaseController {

    private final AppMemberAddressService service;

    /**
     * 创建
     */
    @PostMapping("/create")
    public R<Void> create(@Validated @RequestBody MemberAddressCreateDto dto) {
        Long userId = super.getUserId();
        service.create(userId, dto);
        return R.ok();
    }

    /**
     * 查询
     */
    @GetMapping("/query")
    public R<List<MemberAddressEntity>> query(MemberAddressQueryDto query) {
        Long userId = super.getUserId();
        return R.ok(service.query(userId, query));
    }

    /**
     * 详情
     */
    @GetMapping("/detail")
    public R<MemberAddressEntity> detail(@RequestParam("id") Long id) {
        Long userId = super.getUserId();
        return R.ok(service.detail(userId, id));
    }

    /**
     * 更新
     */
    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody MemberAddressUpdateDto dto) {
        Long userId = super.getUserId();
        service.update(userId, dto);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R<Void> delete(@Validated @RequestBody IdDto dto) {
        Long userId = super.getUserId();
        service.delete(userId, dto);
        return R.ok();
    }
}
