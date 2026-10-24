package com.yeshimin.yeahboot.data.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 商品评价公开回复视图
 */
@Data
public class ReviewReplyVo {

    /**
     * 回复ID
     */
    private Long id;

    /**
     * 上一条回复ID
     */
    private Long parentId;

    /**
     * 回复方类型：1买家，2商家
     */
    private Integer senderType;

    /**
     * 回复内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后修改时间
     */
    private LocalDateTime updateTime;
}
