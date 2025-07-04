package com.yeshimin.yeahboot.merchant.controller.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Query;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.base.ShopConditionBaseEntity;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;

/**
 * 提供基础的CRUD接口 for merchant shop data
 *
 * @param <M> BaseMapper
 * @param <E> Entity
 * @param <S> ServiceImpl
 */
@Slf4j
public class ShopCrudController<M extends BaseMapper<E>, E extends ShopConditionBaseEntity<E>, S extends ServiceImpl<M, E>>
        extends MchCrudController<M, E, S> {

    @Autowired
    private S service;
    @Autowired
    private PermissionService permissionService;

    public ShopCrudController(S service) {
        super(service);
    }

    // ================================================================================

    /**
     * CRUD-创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:create')")
    @PostMapping("/crud/create")
    @Transactional(rollbackFor = Exception.class)
    public R<E> crudCreate(@Validated(Create.class) @RequestBody E e) {
        if (super.isCreateDisabled()) {
            return R.fail("该接口已被禁用");
        }
        if (e.getShopId() == null) {
            return R.fail("店铺ID不能为空");
        }
        // check permission
        permissionService.checkShop(super.getUserId(), e.getShopId());
        return super.crudCreate(e);
    }

    /**
     * CRUD-查询
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:query')")
    @GetMapping("/crud/query")
    public R<Page<E>> crudQuery(Page<E> page, @Validated(Query.class) E query) {
        if (super.isQueryDisabled()) {
            return R.fail("该接口已被禁用");
        }
        if (query.getShopId() == null) {
            return R.fail("店铺ID不能为空");
        }
        // check permission for shop
        permissionService.checkShop(super.getUserId(), query.getShopId());
        return super.crudQuery(page, query);
    }

    /**
     * CRUD-详情
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:detail')")
    @GetMapping("/crud/detail")
    public R<E> crudDetail(Long id) {
        if (super.isDetailDisabled()) {
            return R.fail("该接口已被禁用");
        }
        return super.crudDetail(id);
    }

    /**
     * CRUD-更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:update')")
    @PostMapping("/crud/update")
    @Transactional(rollbackFor = Exception.class)
    public R<E> crudUpdate(@Validated(Update.class) @RequestBody E e) {
        if (super.isUpdateDisabled()) {
            return R.fail("该接口已被禁用");
        }
        if (e.getId() == null) {
            return R.fail("ID不能为空");
        }

        E e0 = service.getById(e.getId());
        if (e0 == null) {
            return R.fail("数据未找到");
        }
        // 检查权限
        permissionService.checkMchId(super.getUserId(), e0.getMchId());
        permissionService.checkMchId(e0.getMchId(), e.getMchId());

        permissionService.checkShopId(e0.getShopId(), e.getShopId());

        boolean r = service.updateById(e);
        log.debug("crudUpdate.result: {}", r);
        if (!r) {
            return R.fail("更新失败");
        }
        return R.ok(service.getById(e.getId()));
    }

    /**
     * CRUD-删除
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:delete')")
    @PostMapping("/crud/delete")
    @Transactional(rollbackFor = Exception.class)
    public R<Void> crudDelete(@RequestBody Collection<Long> ids) {
        if (super.isDeleteDisabled()) {
            return R.fail("该接口已被禁用");
        }
        return super.crudDelete(ids);
    }
}
