package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import com.yeshimin.yeahboot.data.domain.vo.ReviewReplyVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 商品评价列表VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProductReviewListVo extends BaseDomain {

    /**
     * 评价ID
     */
    private Long id;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU名称
     */
    private String skuName;

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SPU名称
     */
    private String spuName;

    /**
     * SPU主图
     */
    private String spuMainImage;

    /**
     * 综合评分
     */
    private Integer overallRating;

    /**
     * 评价内容
     */
    private String content;

    /**
     * 买家昵称
     */
    private String nickname;

    /**
     * 买家头像
     */
    private String avatar;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 评价图片
     */
    private List<String> images;

    /**
     * 公开回复列表
     */
    private List<ReviewReplyVo> replies;

    /**
     * 当前用户是否可以回复
     */
    private Boolean canReply;

    /**
     * 当前用户是否可以编辑最新回复
     */
    private Boolean canEditReply;

    /**
     * 当前回复状态说明
     */
    private String replyStatusText;
}
