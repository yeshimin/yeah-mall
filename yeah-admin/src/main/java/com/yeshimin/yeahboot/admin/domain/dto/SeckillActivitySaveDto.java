package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillActivitySaveDto extends BaseDomain {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = {Update.class})
    private Long id;

    /**
     * 活动名称
     */
    @NotBlank(message = "活动名称不能为空", groups = {Create.class})
    private String name;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动封面
     */
    @NotBlank(message = "活动封面不能为空", groups = {Create.class})
    private String coverImage;

    /**
     * 报名开始时间
     */
    private LocalDateTime applyBeginTime;

    /**
     * 报名截止时间
     */
    private LocalDateTime applyEndTime;

    /**
     * 活动开始时间
     */
    private LocalDateTime activityBeginTime;

    /**
     * 活动结束时间
     */
    private LocalDateTime activityEndTime;

    /**
     * 排序：大于等于1
     */
    @Min(value = 1, message = "排序不能小于1", groups = {Create.class, Update.class})
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
