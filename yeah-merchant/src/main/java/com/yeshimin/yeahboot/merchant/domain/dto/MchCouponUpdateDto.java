package com.yeshimin.yeahboot.merchant.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class MchCouponUpdateDto extends MchCouponCreateDto {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;
}
