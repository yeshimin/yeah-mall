package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 拼团活动保存参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GroupBuyActivitySaveDto extends BaseDomain {

    /**
     * 拼团活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    @NotBlank(message = "活动名称不能为空")
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动封面
     */
    private String coverImage;

    /**
     * 报名开始时间
     */
    @NotNull(message = "报名开始时间不能为空")
    private LocalDateTime applyBeginTime;

    /**
     * 报名截止时间
     */
    @NotNull(message = "报名截止时间不能为空")
    private LocalDateTime applyEndTime;

    /**
     * 活动开始时间
     */
    @NotNull(message = "活动开始时间不能为空")
    private LocalDateTime activityBeginTime;

    /**
     * 活动结束时间
     */
    @NotNull(message = "活动结束时间不能为空")
    private LocalDateTime activityEndTime;

    /**
     * 每团成团人数
     */
    @NotNull(message = "每团成团人数不能为空")
    @Min(value = 2, message = "每团成团人数不能小于2")
    private Integer minPeople;

    /**
     * 最大开团数，-1表示不限，0表示未设置
     */
    @NotNull(message = "最大开团数不能为空")
    @Min(value = -1, message = "最大开团数不能小于-1")
    private Integer groupLimit;

    /**
     * 排序
     */
    @NotNull(message = "排序不能为空")
    @Min(value = 1, message = "排序不能小于1")
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
