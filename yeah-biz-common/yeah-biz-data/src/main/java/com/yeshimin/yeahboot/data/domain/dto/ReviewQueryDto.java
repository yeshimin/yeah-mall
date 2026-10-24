package com.yeshimin.yeahboot.data.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseQueryDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import javax.validation.constraints.Pattern;

/**
 * 评价查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ReviewQueryDto extends BaseQueryDto {

    /**
     * 商品SPU ID
     */
    private Long spuId;

    /**
     * 店铺ID
     */
    private Long shopId;

    /**
     * 是否仅查询有图评价
     */
    private Boolean hasImage;

    /**
     * 是否仅查询好评
     */
    private Boolean goodOnly;

    /**
     * 是否公开
     */
    private Boolean isVisible;

    /**
     * 评价等级：good好评、middle中评、bad差评
     */
    @Pattern(regexp = "good|middle|bad", message = "评价等级不正确")
    private String ratingType;

    /**
     * 是否已回复
     */
    private Boolean isReplied;

    /**
     * 评价开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;

    /**
     * 评价结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;
}
