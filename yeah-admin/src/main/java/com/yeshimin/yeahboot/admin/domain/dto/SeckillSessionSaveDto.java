package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeckillSessionSaveDto extends BaseDomain {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = {Update.class})
    private Long id;

    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空", groups = {Create.class})
    private Long activityId;

    /**
     * 场次名称
     */
    @NotBlank(message = "场次名称不能为空", groups = {Create.class, Update.class})
    @Size(max = 30, message = "场次名称不能超过30个字符", groups = {Create.class, Update.class})
    private String name;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空", groups = {Create.class, Update.class})
    private LocalDateTime beginTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空", groups = {Create.class, Update.class})
    private LocalDateTime endTime;

    /**
     * 排序：1-999
     */
    @Min(value = 1, message = "排序不能小于1", groups = {Create.class, Update.class})
    @Max(value = 999, message = "排序不能大于999", groups = {Create.class, Update.class})
    private Integer sort;

    /**
     * 是否启用
     */
    private Boolean isEnabled;

    /**
     * 备注
     */
    @Size(max = 100, message = "备注不能超过100个字符", groups = {Create.class, Update.class})
    private String remark;
}
