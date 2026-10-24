package com.yeshimin.yeahboot.service;

import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.ReviewReplySenderTypeEnum;
import com.yeshimin.yeahboot.data.domain.dto.ReviewReplyDto;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewEntity;
import com.yeshimin.yeahboot.data.domain.entity.ProductSkuReviewReplyEntity;
import com.yeshimin.yeahboot.data.repository.ProductSkuReviewReplyRepo;
import com.yeshimin.yeahboot.data.repository.ProductSkuReviewRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品评价公开回复服务
 */
@Service
@RequiredArgsConstructor
public class ReviewReplyService {

    private final ProductSkuReviewRepo productSkuReviewRepo;
    private final ProductSkuReviewReplyRepo productSkuReviewReplyRepo;

    /**
     * 买家回复商家
     */
    @Transactional(rollbackFor = Exception.class)
    public void replyByMember(Long memberId, ReviewReplyDto dto) {
        ProductSkuReviewEntity review = lockReview(dto.getId());
        if (!memberId.equals(review.getMemberId())) {
            throw new BaseException("无该评价回复权限");
        }
        appendOrUpdate(review, ReviewReplySenderTypeEnum.MEMBER, memberId, dto.getReplyContent());
    }

    /**
     * 商家回复买家
     */
    @Transactional(rollbackFor = Exception.class)
    public void replyByMerchant(Long mchId, Long shopId, Long userId, ReviewReplyDto dto) {
        ProductSkuReviewEntity review = lockReview(dto.getId());
        if (!mchId.equals(review.getMchId()) || !shopId.equals(review.getShopId())) {
            throw new BaseException("无该评价回复权限");
        }
        appendOrUpdate(review, ReviewReplySenderTypeEnum.MERCHANT, userId, dto.getReplyContent());
    }

    /**
     * 追加一条交替回复，或编辑当前回复方尚未被回应的最后一条回复。
     */
    private void appendOrUpdate(ProductSkuReviewEntity review, ReviewReplySenderTypeEnum senderType,
                                Long senderId, String content) {
        if (!Boolean.TRUE.equals(review.getIsVisible())) {
            throw new BaseException("评价已隐藏，不能继续回复");
        }
        String replyContent = content.trim();
        ProductSkuReviewReplyEntity lastReply = productSkuReviewReplyRepo.findLatestByReviewIdForUpdate(review.getId());

        // 买家首评后，必须先由商家开启第一轮公开回复。
        if (lastReply == null && senderType != ReviewReplySenderTypeEnum.MERCHANT) {
            throw new BaseException("等待商家回复后才能继续回复");
        }

        if (lastReply != null && senderType.getValue() == lastReply.getSenderType()) {
            // 同一方不能连续新增回复，只能在对方回复前修改自己的最新内容。
            lastReply.setContent(replyContent);
            lastReply.setSenderId(senderId);
            lastReply.updateById();
            return;
        }

        ProductSkuReviewReplyEntity reply = new ProductSkuReviewReplyEntity();
        reply.setReviewId(review.getId());
        reply.setParentId(lastReply == null ? 0L : lastReply.getId());
        reply.setMchId(review.getMchId());
        reply.setShopId(review.getShopId());
        reply.setMemberId(review.getMemberId());
        reply.setSenderType(senderType.getValue());
        reply.setSenderId(senderId);
        reply.setContent(replyContent);
        reply.insert();
    }

    /**
     * 锁定评价根记录以串行化同一评价下的回复。
     */
    private ProductSkuReviewEntity lockReview(Long reviewId) {
        ProductSkuReviewEntity review = productSkuReviewRepo.findByIdForUpdate(reviewId);
        if (review == null) {
            throw new BaseException("评价不存在");
        }
        return review;
    }
}
