package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.admin.domain.dto.PlatQuickEntrySaveDto;
import com.yeshimin.yeahboot.data.domain.entity.PlatQuickEntryEntity;
import com.yeshimin.yeahboot.data.repository.PlatQuickEntryRepo;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPlatQuickEntryService {

    private final StorageManager storageManager;

    private final PlatQuickEntryRepo platQuickEntryRepo;

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(PlatQuickEntrySaveDto dto) {
        // 标记使用图片
        storageManager.markUse(dto.getIcon());

        platQuickEntryRepo.createOne(dto.getName(), dto.getIcon(), dto.getType(), dto.getTarget(),
                dto.getSort(), dto.getIsEnabled(), dto.getRemark());
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(PlatQuickEntrySaveDto dto) {
        // 检查：是否存在
        PlatQuickEntryEntity entity = platQuickEntryRepo.getOneById(dto.getId());

        if (StrUtil.isNotBlank(dto.getIcon()) && !Objects.equals(entity.getIcon(), dto.getIcon())) {
            // 标记使用新图片
            storageManager.markUse(dto.getIcon());
            // 标记废弃旧图片
            storageManager.unmarkUse(entity.getIcon());
        }

        BeanUtil.copyProperties(dto, entity);
        entity.updateById();
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(Collection<Long> ids) {
        for (Long id : ids) {
            // 检查：是否存在
            PlatQuickEntryEntity entity = platQuickEntryRepo.getOneById(id);

            // 表示解除使用图片
            storageManager.unmarkUse(entity.getIcon());
            // 删除记录
            entity.deleteById();
        }
    }
}
