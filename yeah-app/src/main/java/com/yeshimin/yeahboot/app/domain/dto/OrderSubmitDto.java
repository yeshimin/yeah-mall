package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.app.common.enums.OrderSceneEnum;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderSubmitDto extends BaseDomain {

    /**
     * 订单项
     */
    @Valid
    @NotEmpty(message = "订单项不能为空")
    private List<OrderItemDto> items;

    /**
     * 订单场景
     */
    @EnumValue(enumClass = OrderSceneEnum.class)
    private String scene;
}
