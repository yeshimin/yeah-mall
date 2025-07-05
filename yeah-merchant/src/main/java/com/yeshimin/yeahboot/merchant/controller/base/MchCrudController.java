package com.yeshimin.yeahboot.merchant.controller.base;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.base.MchConditionBaseEntity;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 提供基础的CRUD接口 for merchant data
 *
 * @param <M> BaseMapper
 * @param <E> Entity
 * @param <S> ServiceImpl
 */
@Slf4j
public class MchCrudController<M extends BaseMapper<E>, E extends MchConditionBaseEntity<E>, S extends ServiceImpl<M, E>>
        extends CrudController<M, E, S> {

    @Autowired
    private S service;
    @Autowired
    private PermissionService permissionService;

    public MchCrudController(S service) {
        super(service);
    }

    // ================================================================================

    /**
     * CRUD-创建
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:create')")
    @PostMapping("/crud/create")
    @Transactional(rollbackFor = Exception.class)
    public R<E> crudCreate(@RequestBody E e) {
        if (super.isCreateDisabled()) {
            return R.fail("该接口已被禁用");
        }
        // check permission for mch
        permissionService.checkMch(super.getUserId(), e);

        boolean r = service.save(e);
        log.debug("crudCreate.result: {}", r);
        return R.ok(e);
    }

    /**
     * CRUD-查询
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:query')")
    @GetMapping("/crud/query")
    public R<Page<E>> crudQuery(Page<E> page, E query) {
        if (super.isQueryDisabled()) {
            return R.fail("该接口已被禁用");
        }
        // check permission for mch
        permissionService.checkMch(super.getUserId(), query);

        @SuppressWarnings("unchecked")
        Class<E> clazz = (Class<E>) query.getClass();
        return R.ok(service.page(page, QueryHelper.getQueryWrapper(query, clazz)));
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
        if (id == null) {
            return R.fail("ID不能为空");
        }

        E e = service.getById(id);
        if (e == null) {
            return R.fail("数据未找到");
        }
        // 检查权限
        permissionService.checkMchId(super.getUserId(), e.getMchId());
        return R.ok(e);
    }

    /**
     * CRUD-更新
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':crud:update')")
    @PostMapping("/crud/update")
    @Transactional(rollbackFor = Exception.class)
    public R<E> crudUpdate(@RequestBody E e) {
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
        // 检查权限 for mch
        permissionService.checkMchId(super.getUserId(), e0.getMchId()); // 检查e0数据权限，即当前商家是否对指定id的数据有权限
        permissionService.checkMch(e0.getMchId(), e); // 检查e数据权限，即当前商家是否要变更了mchId参数，这是不允许的

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
        if (ids == null || ids.isEmpty()) {
            return R.fail("IDs不能为空");
        }

        // com.baomidou.mybatisplus.core.exceptions.MybatisPlusException: can not find lambda cache for this entity [com.yeshimin.yeahboot.data.domain.base.MchConditionBaseEntity]
//        LambdaQueryWrapper<E> wrapper = new LambdaQueryWrapper<>();
//        wrapper.in(E::getId, ids);
//        wrapper.ne(E::getMchId, super.getUserId());
//        long count = service.count(wrapper);
//        if (count > 0) {
//            return R.fail("存在无权限的数据");
//        }

        List<E> list = service.listByIds(ids);
        Long mchId = super.getUserId();
        for (E e : list) {
            if (!Objects.equals(e.getMchId(), mchId)) {
                return R.fail("存在无权限的数据");
            }
        }

        boolean r = service.removeByIds(ids);
        log.debug("crudDelete.result: {}", r);
        return R.ok();
    }
}
