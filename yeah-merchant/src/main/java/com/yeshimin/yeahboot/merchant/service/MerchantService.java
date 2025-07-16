package com.yeshimin.yeahboot.merchant.service;

import com.yeshimin.yeahboot.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.data.repository.MerchantRepo;
import com.yeshimin.yeahboot.merchant.domain.vo.MerchantMineVo;
import com.yeshimin.yeahboot.merchant.domain.vo.MerchantResourceVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

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

    /**
     * mine
     */
    public MerchantMineVo mine(Long userId) {
        MerchantEntity merchant = merchantRepo.getOneById(userId);

        MerchantMineVo vo = new MerchantMineVo();
        vo.setUser(merchant);
        vo.setRoles(Collections.emptyList());
        vo.setOrgs(Collections.emptyList());
        vo.setPermissions(Collections.singletonList("*:*:*"));
        return vo;
    }

    /**
     * mineResources
     */
    public List<MerchantResourceVo> mineResources(Long userId) {
        return Collections.emptyList();
    }
}
