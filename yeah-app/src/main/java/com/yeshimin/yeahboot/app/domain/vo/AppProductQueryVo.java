package com.yeshimin.yeahboot.app.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * App端商品查询VO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "App端商品查询VO")
public class AppProductQueryVo extends BaseDomain {

    /**
     * 总数（包含当前返回的）
     */
    @Schema(description = "总数（包含当前返回的）")
    private Integer total;

    /**
     * 数据
     */
    @Schema(description = "数据")
    private List<ProductVo> data;

    /**
     * 滚动token
     */
    @Schema(description = "滚动token")
    private String scrollToken;
}
