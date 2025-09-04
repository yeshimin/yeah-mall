package com.yeshimin.yeahboot.merchant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Query;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuEntity;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.mapper.ProductSpuMapper;
import com.yeshimin.yeahboot.data.repository.ProductSpuRepo;
import com.yeshimin.yeahboot.merchant.controller.base.ShopCrudController;
import com.yeshimin.yeahboot.merchant.domain.dto.*;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSpecVo;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSpuDetailVo;
import com.yeshimin.yeahboot.merchant.domain.vo.ProductSpuVo;
import com.yeshimin.yeahboot.merchant.service.ProductSpuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * 商品SPU表
 */
@RestController
@RequestMapping("/mch/productSpu")
public class ProductSpuController extends ShopCrudController<ProductSpuMapper, ProductSpuEntity, ProductSpuRepo> {

    @Autowired
    private ProductSpuService service;

    public ProductSpuController(ProductSpuRepo repo) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(repo);
        super.setModule("mch:productSpu").disableCreate().disableQuery().disableDetail().disableUpdate().disableDelete();
    }

    // ================================================================================

    /**
     * 创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':create')")
    @PostMapping("/create")
    public R<ProductSpuEntity> crudCreate(@Validated(Create.class) @RequestBody ProductSpuCreateDto dto) {
        Long userId = super.getUserId();
        SysStorageEntity sysStorage = null;
        if (dto.getImageFile() != null) {
            sysStorage = service.storeFile(dto.getImageFile(), StorageTypeEnum.LOCAL);
        }
        return R.ok(service.create(userId, dto, sysStorage));
    }

    /**
     * 查询
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':query')")
    @GetMapping("/query")
    public R<IPage<ProductSpuVo>> query(@Validated(Query.class) Page<ProductSpuEntity> page, ProductSpuQueryDto query) {
        Long userId = super.getUserId();
        return R.ok(service.query(page, userId, query));
    }

    /**
     * 详情
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':detail')")
    @GetMapping("/detail")
    public R<ProductSpuDetailVo> detail(@RequestParam Long id) {
        Long userId = super.getUserId();
        return R.ok(service.detail(userId, id));
    }

    /**
     * 更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':update')")
    @PostMapping("/update")
    public R<Boolean> update(@Validated(Update.class) @RequestBody ProductSpuUpdateDto dto) {
        Long userId = super.getUserId();
        return R.ok(service.update(userId, dto));
    }

    /**
     * 删除
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':delete')")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Collection<Long> ids) {
        Long userId = super.getUserId();
        service.delete(userId, ids);
        return R.ok();
    }

    /**
     * 设置商品spu规格
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':setSpec')")
    @PostMapping("/setSpec")
    public R<Void> setSpec(@Validated(Create.class) @RequestBody ProductSpuSpecSetDto dto) {
        Long userId = super.getUserId();
        service.setSpec(userId, dto);
        return R.ok();
    }

    /**
     * 查询商品spu规格
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':querySpec')")
    @GetMapping("/querySpec")
    public R<List<ProductSpecVo>> querySpec(@Validated(Query.class) ProductSpuSpecQueryDto query) {
        Long userId = super.getUserId();
        List<ProductSpecVo> list = service.querySpec(userId, query);
        return R.ok(list);
    }
}
