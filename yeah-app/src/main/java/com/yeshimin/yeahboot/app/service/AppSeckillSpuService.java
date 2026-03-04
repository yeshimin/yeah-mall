package com.yeshimin.yeahboot.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.dto.SeckillSpuQueryDto;
import com.yeshimin.yeahboot.data.domain.entity.SeckillSpuEntity;
import com.yeshimin.yeahboot.data.domain.vo.SeckillSpuVo;
import com.yeshimin.yeahboot.data.repository.SeckillSpuRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppSeckillSpuService {

    private final SeckillSpuRepo seckillSpuRepo;

    /**
     * 查询报名列表
     */
    public IPage<SeckillSpuVo> query(Page<SeckillSpuEntity> page, SeckillSpuQueryDto query) {
        return seckillSpuRepo.query(page, query, null);
    }
}
