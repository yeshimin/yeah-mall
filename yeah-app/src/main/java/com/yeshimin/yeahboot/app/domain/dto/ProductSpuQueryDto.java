package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.app.common.enums.ProductSortEnum;
import com.yeshimin.yeahboot.common.common.config.mybatis.Query;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * app端商品查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Query(custom = true)
public class ProductSpuQueryDto extends BaseDomain {

    /**
     * 关键词
     */
    @NotNull(message = "关键词不能为空")
    private String keyword;

    /**
     * 排序选项
     */
    @NotNull(message = "商品排序选项不能为空")
    @EnumValue(enumClass = ProductSortEnum.class, message = "商品排序枚举不正确")
    private String sortBy;

    /**
     * 滚动分页token
     */
    private String scrollToken;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
