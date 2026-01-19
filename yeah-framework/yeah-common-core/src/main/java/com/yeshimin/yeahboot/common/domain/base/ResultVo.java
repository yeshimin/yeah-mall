package com.yeshimin.yeahboot.common.domain.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "结果VO")
@AllArgsConstructor
public class ResultVo extends BaseDomain {

    /**
     * 是否成功
     */
    @Schema(description = "是否成功")
    private Boolean success;
}
