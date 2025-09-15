package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * 商品-添加到收藏夹
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AddToFavoritesDto extends BaseDomain {

    /**
     * 商品SPU ID
     */
    @NotNull(message = "商品SPU ID不能为空")
    private Long spuId;
}
