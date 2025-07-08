package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Create;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 规格与选项DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SpecOptDto extends BaseDomain {

    /**
     * 规格ID
     */
    @NotNull(message = "规格ID不能为空", groups = {Create.class})
    private Long specId;

    /**
     * 选项ID集合
     */
    @NotEmpty(message = "选项ID集合不能为空", groups = {Create.class})
    private List<Long> optIds;
}
