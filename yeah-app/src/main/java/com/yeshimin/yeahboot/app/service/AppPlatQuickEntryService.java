package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yeshimin.yeahboot.app.domain.vo.AppPlatQuickEntryVo;
import com.yeshimin.yeahboot.data.domain.entity.PlatQuickEntryEntity;
import com.yeshimin.yeahboot.data.repository.PlatQuickEntryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppPlatQuickEntryService {

    private final PlatQuickEntryRepo platQuickEntryRepo;

    /**
     * 查询
     */
    public List<AppPlatQuickEntryVo> query() {
        LambdaQueryWrapper<PlatQuickEntryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PlatQuickEntryEntity::getIsEnabled, true);
        queryWrapper.orderByAsc(PlatQuickEntryEntity::getSort);
        return platQuickEntryRepo.list(queryWrapper).stream()
                .map(item -> BeanUtil.copyProperties(item, AppPlatQuickEntryVo.class)).collect(Collectors.toList());
    }
}
