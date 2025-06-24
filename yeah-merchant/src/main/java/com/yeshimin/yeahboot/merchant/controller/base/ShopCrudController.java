package com.yeshimin.yeahboot.merchant.controller.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.merchant.data.domain.base.ShopConditionBaseEntity;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Objects;

/**
 * 提供基础的CRUD接口 for merchant shop
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
    @PostMapping("/crud/create")
    @Transactional(rollbackFor = Exception.class)
    public R<E> crudCreate(@RequestBody E e) {
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
    @GetMapping("/crud/query")
    public R<Page<E>> crudQuery(Page<E> page, E query) {
        if (query.getShopId() == null) {
            return R.fail("店铺ID不能为空");
        }
        return super.crudQuery(page, query);
    }

    /**
     * CRUD-详情
     */
    @GetMapping("/crud/detail")
    public R<E> crudDetail(@RequestParam Long id) {
        return super.crudDetail(id);
    }

    /**
     * CRUD-更新
     */
    @PostMapping("/crud/update")
    @Transactional(rollbackFor = Exception.class)
    public R<E> crudUpdate(@RequestBody E e) {
        // 权限校验
        E e0 = service.getById(e.getId());
        if (e0 == null) {
            return R.fail("数据未找到");
        }
        if (!Objects.equals(e0.getMchId(), super.getUserId())) {
            return R.fail("没有权限");
        }

        // 权限控制
        e.setMchId(e0.getMchId());
        e.setShopId(e0.getShopId());

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
    @PostMapping("/crud/delete")
    @Transactional(rollbackFor = Exception.class)
    public R<Void> crudDelete(@RequestBody Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return R.ok();
        }

        LambdaQueryWrapper<E> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(E::getId, ids);
        // 权限控制
        wrapper.eq(E::getMchId, super.getUserId());

        boolean r = service.remove(wrapper);
        log.debug("crudDelete.result: {}", r);
        return R.ok();
    }
}
