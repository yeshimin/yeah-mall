package com.yeshimin.yeahboot.merchant.service;

import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantService {

    private final MerchantRepo merchantRepo;

    /**
     * 详情
     */
    public MerchantEntity detail(Long id) {
        return merchantRepo.getOneById(id);
    }
}
