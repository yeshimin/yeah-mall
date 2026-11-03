package com.yeshimin.yeahboot.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantQueryDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantResetPasswordDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantStatusUpdateDto;
import com.yeshimin.yeahboot.admin.domain.dto.MerchantUpdateDto;
import com.yeshimin.yeahboot.admin.domain.vo.MerchantDetailVo;
import com.yeshimin.yeahboot.admin.domain.vo.MerchantListVo;
import com.yeshimin.yeahboot.admin.service.AdminMerchantService;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.mapper.MerchantMapper;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * 商家表
 */
@Validated
@RestController
@RequestMapping("/admin/merchant")
public class AdminMerchantController extends CrudController<MerchantMapper, MerchantEntity, MerchantRepo> {

    @Autowired
    private AdminMerchantService service;

    public AdminMerchantController(MerchantRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("admin:merchant").disableCreate().disableQuery().disableDetail().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 查询
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:query')")
    @GetMapping("/query")
    public R<IPage<MerchantListVo>> query(Page<MerchantEntity> page, MerchantQueryDto query) {
        return R.ok(service.query(page, query));
    }

    /**
     * 详情
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:detail')")
    @GetMapping("/detail")
    public R<MerchantDetailVo> detail(@NotNull(message = "商家ID不能为空") Long id) {
        return R.ok(service.detail(id));
    }

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':create')")
    @PostMapping("/create")
    public R<Void> create(@Validated @RequestBody MerchantCreateDto dto) {
        service.create(dto);
        return R.ok();
    }

    /**
     * 更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':update')")
    @PostMapping("/update")
    public R<Void> update(@Validated @RequestBody MerchantUpdateDto dto) {
        service.update(dto);
        return R.ok();
    }

    /**
     * 重置密码
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':update')")
    @PostMapping("/resetPassword")
    public R<Void> resetPassword(@Validated @RequestBody MerchantResetPasswordDto dto) {
        service.resetPassword(dto);
        return R.ok();
    }

    /**
     * 更新状态
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':update')")
    @PostMapping("/updateStatus")
    public R<Void> updateStatus(@Validated @RequestBody MerchantStatusUpdateDto dto) {
        service.updateStatus(dto);
        return R.ok();
    }

    /**
     * 删除
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':delete')")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Collection<Long> ids) {
        service.delete(ids);
        return R.ok();
    }
}
