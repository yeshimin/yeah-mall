package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * App端商品收藏状态VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "App端商品收藏状态VO")
@AllArgsConstructor
public class FavoritesStatusVo extends BaseDomain {

    /**
     * 结果：true-已收藏，false-未收藏
     */
    @Schema(description = "结果：true-已收藏，false-未收藏")
    private Boolean result;
}
