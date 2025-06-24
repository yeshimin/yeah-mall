package com.yeshimin.yeahboot.merchant.controller.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.merchant.data.domain.base.MchConditionBaseEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collection;
import java.util.Objects;

/**
 * 提供基础的CRUD接口 for merchant data
 *
 * @param <M> BaseMapper
 * @param <E> Entity
 * @param <S> ServiceImpl
 */
@Slf4j
@RequiredArgsConstructor
public class MchCrudController<M extends BaseMapper<E>, E extends MchConditionBaseEntity<E>, S extends ServiceImpl<M, E>>
        extends BaseController {

    private final S service;

    /**
     * CRUD-创建
     */
    @PostMapping("/crud/create")
    @Transactional(rollbackFor = Exception.class)
    public R<E> crudCreate(@RequestBody E e) {
        if (e.getMchId() != null) {
            this.checkUserId(super.getUserId(), e.getMchId());
        } else {
            // 权限控制
            e.setMchId(super.getUserId());
        }

        boolean r = service.save(e);
        log.debug("crudCreate.result: {}", r);
        return R.ok(e);
    }

    /**
     * CRUD-查询
     */
    @GetMapping("/crud/query")
    public R<Page<E>> crudQuery(Page<E> page, E query) {
        if (query.getMchId() != null) {
            this.checkUserId(super.getUserId(), query.getMchId());
        } else {
            // 权限控制
            query.setMchId(super.getUserId());
        }

        @SuppressWarnings("unchecked")
        Class<E> clazz = (Class<E>) query.getClass();
        return R.ok(service.page(page, QueryHelper.getQueryWrapper(query, clazz)));
    }

    /**
     * CRUD-详情
     */
    @GetMapping("/crud/detail")
    public R<E> crudDetail(Long id) {
        if (id == null) {
            return R.fail("ID不能为空");
        }

        E e = service.getById(id);
        if (e == null) {
            return R.fail("数据未找到");
        }
        // 检查权限
        this.checkUserId(super.getUserId(), e.getMchId());
        return R.ok(e);
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
        this.checkUserId(super.getUserId(), e0.getMchId());
        this.checkUserId(e0.getMchId(), e.getMchId());

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
            return R.fail("IDs不能为空");
        }

        LambdaQueryWrapper<E> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(E::getId, ids);
        wrapper.ne(E::getMchId, super.getUserId());
        long count = service.count(wrapper);
        if (count > 0) {
            return R.fail("存在无权限的数据");
        }

        boolean r = service.removeByIds(ids);
        log.debug("crudDelete.result: {}", r);
        return R.ok();
    }

    // ================================================================================

    /**
     * 检查用户ID，如果会话用户ID与参数指定的用户ID不一致，则抛出异常
     */
    public void checkUserId(Long mchId, Long paramMchId) {
        if (mchId == null) {
            throw new RuntimeException("数据错误（商户ID为空），请联系管理员！");
        }
        if (paramMchId != null && !Objects.equals(mchId, paramMchId)) {
            throw new RuntimeException("无该商户权限");
        }
    }
}
