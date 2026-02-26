package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.vo.SeckillActivityVo;
import com.yeshimin.yeahboot.common.common.config.mybatis.QueryHelper;
import com.yeshimin.yeahboot.data.common.enums.SeckillActivityStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.SeckillActivityEntity;
import com.yeshimin.yeahboot.data.repository.SeckillActivityRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppSeckillActivityService {

    private final SeckillActivityRepo seckillActivityRepo;

    /**
     * 查询
     */
    public IPage<SeckillActivityVo> query(Page<SeckillActivityEntity> page, SeckillActivityEntity query) {
        LambdaQueryWrapper<SeckillActivityEntity> queryWrapper =
                QueryHelper.getQueryWrapper(query, SeckillActivityEntity.class);
        // 【启用】的
        queryWrapper.eq(SeckillActivityEntity::getIsEnabled, true);
        // 【已发布】的
        queryWrapper.in(SeckillActivityEntity::getStatus, SeckillActivityStatusEnum.PUBLISHED_STATUS);
        // 按【排序】字段正序
        queryWrapper.orderByAsc(SeckillActivityEntity::getSort);
        // 按【创建时间】字段倒序
        queryWrapper.orderByDesc(SeckillActivityEntity::getCreateTime);

        return seckillActivityRepo.page(page, queryWrapper)
                .convert(e -> BeanUtil.copyProperties(e, SeckillActivityVo.class));
    }
}
