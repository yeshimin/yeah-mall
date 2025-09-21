package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.Query;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 预览订单DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Query(custom = true)
public class OrderPreviewDto extends BaseDomain {

    /**
     * 商品项
     */
    @Valid
    @NotEmpty(message = "商品项不能为空")
    private List<OrderPreviewItemDto> items;
}
