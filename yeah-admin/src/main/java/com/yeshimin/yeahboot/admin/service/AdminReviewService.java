package com.yeshimin.yeahboot.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.dto.ReviewQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.ReviewVisibilityDto;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewImageEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewReplyEntity;
import com.yeshimin.yeahboot.data.domain.vo.ReviewDetailVo;
import com.yeshimin.yeahboot.data.domain.vo.ReviewReplyVo;
import com.yeshimin.yeahboot.data.repository.ProductSkuReviewImageRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuReviewRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuReviewReplyRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * 平台评价管理服务
 */
@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ProductSkuReviewRepo productSkuReviewRepo;
    private final ProductSkuReviewImageRepo productSkuReviewImageRepo;
    private final ProductSkuReviewReplyRepo productSkuReviewReplyRepo;

    /**
     * 查询评价列表
     */
    public IPage<ProductSkuReviewEntity> query(Page<ProductSkuReviewEntity> page, ReviewQueryDto query) {
        return productSkuReviewRepo.queryBackstagePage(page, null, null, query);
    }

    /**
     * 查询评价详情
     */
    public ReviewDetailVo detail(Long id) {
        ProductSkuReviewEntity review = productSkuReviewRepo.getOneById(id, "评价不存在");
        ReviewDetailVo result = new ReviewDetailVo();
        result.setReview(review);
        result.setImages(productSkuReviewImageRepo.findListByReviewIds(java.util.Collections.singletonList(id))
                .stream().map(ProductSkuReviewImageEntity::getImage).collect(Collectors.toList()));
        result.setReplies(productSkuReviewReplyRepo.findListByReviewIds(java.util.Collections.singletonList(id)).stream()
                .map(reply -> cn.hutool.core.bean.BeanUtil.copyProperties(reply, ReviewReplyVo.class))
                .collect(Collectors.toList()));
        return result;
    }

    /**
     * 更新评价公开状态
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateVisibility(ReviewVisibilityDto dto) {
        ProductSkuReviewEntity review = productSkuReviewRepo.getOneById(dto.getId(), "评价不存在");
        String visibilityRemark = dto.getVisibilityRemark() == null ? "" : dto.getVisibilityRemark().trim();
        if (!dto.getIsVisible() && visibilityRemark.isEmpty()) {
            throw new BaseException("隐藏评价时必须填写处理原因");
        }
        review.setIsVisible(dto.getIsVisible());
        review.setVisibilityRemark(visibilityRemark);
        review.updateById();
    }
}
