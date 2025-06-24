package com.yeshimin.yeahboot.merchant.controller.base;

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
        // check permission
        permissionService.checkShop(super.getUserId(), query.getShopId());
        return super.crudQuery(page, query);
    }

    /**
     * CRUD-详情
     */
    @GetMapping("/crud/detail")
    public R<E> crudDetail(Long id) {
        return super.crudDetail(id);
    }

    /**
     * CRUD-更新
     */
    @PostMapping("/crud/update")
    @Transactional(rollbackFor = Exception.class)
    public R<E> crudUpdate(@RequestBody E e) {
        if (e.getId() == null) {
            return R.fail("ID不能为空");
        }

        E e0 = service.getById(e.getId());
        if (e0 == null) {
            return R.fail("数据未找到");
        }
        // 检查权限
        super.checkUserId(super.getUserId(), e0.getMchId());
        super.checkUserId(e0.getMchId(), e.getMchId());

        this.checkShopId(e0.getShopId(), e.getShopId());

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
        return super.crudDelete(ids);
    }

    // ================================================================================

    /**
     * 检查店铺ID
     */
    public void checkShopId(Long shopId, Long paramShopId) {
        if (shopId == null) {
            throw new RuntimeException("数据错误（店铺ID为空），请联系管理员！");
        }
        if (paramShopId != null && !Objects.equals(shopId, paramShopId)) {
            throw new RuntimeException("无该店铺权限");
        }
    }
}
