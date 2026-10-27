package com.yeshimin.yeahboot.admin.domain.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 积分规则更新参数
 */
@Data
public class MemberPointsRuleUpdateDto {
    /**
     * 规则ID
     */
    @NotNull(message = "规则ID不能为空")
    private Long id;
    /**
     * 固定积分
     */
    @Min(value = 0, message = "固定积分不能小于0")
    private Long fixedPoints;
    /**
     * 每一元获得积分数
     */
    @DecimalMin(value = "0", message = "积分比例不能小于0")
    private BigDecimal pointsPerYuan;
    /**
     * 是否启用
     */
    @NotNull(message = "启用状态不能为空")
    private Boolean isEnabled;
    /**
     * 规则说明
     */
    private String description;
}
