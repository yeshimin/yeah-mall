package com.yeshimin.yeahboot.app.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.app.domain.dto.FeedbackSubmitDto;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.common.enums.FeedbackStatusEnum;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackEntity;
import com.yeshimin.yeahboot.data.domain.entity.MemberFeedbackImageEntity;
import com.yeshimin.yeahboot.data.domain.vo.FeedbackDetailVo;
import com.yeshimin.yeahboot.data.repository.MemberFeedbackImageRepo;
import com.yeshimin.yeahboot.data.repository.MemberFeedbackRepo;
import com.yeshimin.yeahboot.storage.StorageManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * App 用户意见反馈服务
 */
@Service
@RequiredArgsConstructor
public class AppFeedbackService {

    private final MemberFeedbackRepo memberFeedbackRepo;
    private final MemberFeedbackImageRepo memberFeedbackImageRepo;
    private final StorageManager storageManager;

    /**
     * 提交用户反馈
     */
    @Transactional(rollbackFor = Exception.class)
    public void submit(Long userId, FeedbackSubmitDto dto) {
        List<String> images = dto.getImages() == null ? Collections.emptyList() : dto.getImages();
        if (new HashSet<>(images).size() != images.size()) {
            throw new BaseException("反馈图片不能重复");
        }
        if (!images.isEmpty()) {
            // 提交成功后将上传临时文件标记为已使用，避免被存储清理任务误删。
            storageManager.markUse(images.toArray(new String[0]));
        }

        // 先保存反馈主记录，图片记录通过反馈ID建立一对多关联。
        MemberFeedbackEntity feedback = new MemberFeedbackEntity();
        feedback.setMemberId(userId);
        feedback.setContent(dto.getContent().trim());
        feedback.setContact(dto.getContact() == null ? "" : dto.getContact().trim());
        feedback.setStatus(FeedbackStatusEnum.PENDING.getValue());
        feedback.setReplyContent("");
        feedback.insert();

        if (!images.isEmpty()) {
            // 保留用户上传顺序，后台和App展示时保持一致。
            List<MemberFeedbackImageEntity> feedbackImages = new ArrayList<MemberFeedbackImageEntity>();
            for (int index = 0; index < images.size(); index++) {
                MemberFeedbackImageEntity image = new MemberFeedbackImageEntity();
                image.setFeedbackId(feedback.getId());
                image.setImage(images.get(index));
                image.setSort(index + 1);
                feedbackImages.add(image);
            }
            memberFeedbackImageRepo.saveBatch(feedbackImages);
        }
    }

    /**
     * 查询当前用户的反馈记录
     */
    public IPage<FeedbackDetailVo> query(Long userId, Page<MemberFeedbackEntity> page) {
        Page<MemberFeedbackEntity> feedbackPage = memberFeedbackRepo.lambdaQuery()
                .eq(MemberFeedbackEntity::getMemberId, userId)
                .orderByDesc(MemberFeedbackEntity::getCreateTime)
                .page(page);
        // 当前页图片一次批量查询，避免反馈列表产生N+1图片查询。
        Map<Long, List<String>> images = toImageMap(feedbackPage.getRecords());
        return feedbackPage.convert(feedback -> toDetailVo(feedback, images.get(feedback.getId())));
    }

    /**
     * 查询当前用户的反馈详情
     */
    public FeedbackDetailVo detail(Long userId, Long id) {
        MemberFeedbackEntity feedback = memberFeedbackRepo.getOneById(id, "反馈不存在");
        if (!userId.equals(feedback.getMemberId())) {
            throw new BaseException("无该反馈权限");
        }
        return toDetailVo(feedback, toImageMap(Collections.singletonList(feedback)).get(feedback.getId()));
    }

    private Map<Long, List<String>> toImageMap(Collection<MemberFeedbackEntity> feedbacks) {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return Collections.emptyMap();
        }
        Set<Long> feedbackIds = feedbacks.stream().map(MemberFeedbackEntity::getId).collect(Collectors.toSet());
        return memberFeedbackImageRepo.findListByFeedbackIds(feedbackIds).stream()
                .collect(Collectors.groupingBy(MemberFeedbackImageEntity::getFeedbackId,
                        Collectors.mapping(MemberFeedbackImageEntity::getImage, Collectors.toList())));
    }

    private FeedbackDetailVo toDetailVo(MemberFeedbackEntity feedback, List<String> images) {
        FeedbackDetailVo result = new FeedbackDetailVo();
        result.setFeedback(feedback);
        result.setImages(images == null ? Collections.<String>emptyList() : images);
        return result;
    }
}
