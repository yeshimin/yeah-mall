package com.yeshimin.yeahboot.app.service;

import com.yeshimin.yeahboot.app.domain.vo.PlatBannerVo;
import com.yeshimin.yeahboot.data.repository.PlatBannerRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppPlatBannerService {

    private final PlatBannerRepo platBannerRepo;

    /**
     * 查询
     */
    public List<PlatBannerVo> query() {
        return platBannerRepo.list().stream().map(e -> {
            PlatBannerVo vo = new PlatBannerVo();
            vo.setImageUrl(e.getImageUrl());
            return vo;
        }).collect(Collectors.toList());
    }
}
