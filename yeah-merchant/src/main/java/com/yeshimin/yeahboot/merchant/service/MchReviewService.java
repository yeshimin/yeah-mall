package com.yeshimin.yeahboot.merchant.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.dto.ReviewQueryDto;
import com.yeshimin.yeahboot.data.domain.dto.ReviewReplyDto;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewImageEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewReplyEntity;
import com.yeshimin.yeahboot.data.domain.vo.ReviewDetailVo;
import com.yeshimin.yeahboot.data.domain.vo.ReviewReplyVo;
import com.yeshimin.yeahboot.data.repository.ProductSkuReviewImageRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuReviewRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuReviewReplyRepo;
import com.yeshimin.yeahboot.service.ReviewReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商家评价服务
 */
@Service
@RequiredArgsConstructor
public class MchReviewService {

    private final PermissionService permissionService;
    private final ProductSkuReviewRepo productSkuReviewRepo;
    private final ProductSkuReviewImageRepo productSkuReviewImageRepo;
    private final ProductSkuReviewReplyRepo productSkuReviewReplyRepo;
    private final ReviewReplyService reviewReplyService;

    /**
     * 查询当前店铺评价
     */
    public IPage<ProductSkuReviewEntity> query(Long mchId, Long shopId, Page<ProductSkuReviewEntity> page,
                                                ReviewQueryDto query) {
        permissionService.checkShop(mchId, shopId);
        return productSkuReviewRepo.queryBackstagePage(page, mchId, shopId, query);
    }

    /**
     * 查询当前店铺评价详情
     */
    public ReviewDetailVo detail(Long mchId, Long shopId, Long id) {
        permissionService.checkShop(mchId, shopId);
        ProductSkuReviewEntity review = productSkuReviewRepo.getOneById(id, "评价不存在");
        if (!mchId.equals(review.getMchId()) || !shopId.equals(review.getShopId())) {
            throw new BaseException("无该评价权限");
        }
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
     * 回复评价
     */
    public void reply(Long mchId, Long shopId, Long userId, ReviewReplyDto dto) {
        permissionService.checkShop(mchId, shopId);
        reviewReplyService.replyByMerchant(mchId, shopId, userId, dto);
    }
}
