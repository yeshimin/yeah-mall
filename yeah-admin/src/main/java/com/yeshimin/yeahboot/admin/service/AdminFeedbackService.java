package com.yeshimin.yeahboot.admin.service;

import com.yeshimin.yeahboot.admin.domain.dto.FeedbackHandleDto;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.FeedbackStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackImageEntity;
import com.yeshimin.yeahboot.data.domain.vo.FeedbackDetailVo;
import com.yeshimin.yeahboot.data.repository.MemberFeedbackImageRepo;
import com.yeshimin.yeahboot.data.repository.MemberFeedbackRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 平台用户反馈管理服务
 */
@Service
@RequiredArgsConstructor
public class AdminFeedbackService {

    private final MemberFeedbackRepo memberFeedbackRepo;
    private final MemberFeedbackImageRepo memberFeedbackImageRepo;

    /**
     * 查询反馈详情
     */
    public FeedbackDetailVo detail(Long id) {
        MemberFeedbackEntity feedback = memberFeedbackRepo.getOneById(id, "反馈不存在");
        FeedbackDetailVo result = new FeedbackDetailVo();
        result.setFeedback(feedback);
        result.setImages(memberFeedbackImageRepo.findListByFeedbackIds(Collections.singletonList(id)).stream()
                .map(MemberFeedbackImageEntity::getImage)
                .collect(Collectors.toList()));
        return result;
    }

    /**
     * 更新反馈处理状态和平台回复
     */
    @Transactional(rollbackFor = Exception.class)
    public void handle(FeedbackHandleDto dto) {
        FeedbackStatusEnum status = FeedbackStatusEnum.of(dto.getStatus());
        if (status == null || status == FeedbackStatusEnum.PENDING) {
            throw new BaseException("反馈只能更新为处理中或已解决");
        }
        String replyContent = dto.getReplyContent() == null ? "" : dto.getReplyContent().trim();
        if (status == FeedbackStatusEnum.RESOLVED && replyContent.isEmpty()) {
            throw new BaseException("标记已解决时必须填写平台回复");
        }

        // 状态与回复在同一事务更新，保证用户不会看到已解决但没有处理结论的记录。
        MemberFeedbackEntity feedback = memberFeedbackRepo.getOneById(dto.getFeedbackId(), "反馈不存在");
        feedback.setStatus(status.getValue());
        feedback.setReplyContent(replyContent);
        feedback.setReplyTime(replyContent.isEmpty() ? null : LocalDateTime.now());
        feedback.updateById();
    }
}
