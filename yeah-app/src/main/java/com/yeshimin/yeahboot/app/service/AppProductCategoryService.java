package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.app.domain.vo.ProductCategoryVo;
import com.yeshimin.yeahboot.data.domain.entity.ProductCategoryEntity;
import com.yeshimin.yeahboot.data.repository.ProductCategoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppProductCategoryService {

    private final ProductCategoryRepo productCategoryRepo;

    public List<ProductCategoryVo> query(Long shopId) {
        List<ProductCategoryEntity> list = productCategoryRepo.findListByShopId(shopId);
        return BeanUtil.copyToList(list, ProductCategoryVo.class);
    }
}
