package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.app.common.enums.OrderSceneEnum;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
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
     * 地址ID
     */
    @NotNull(message = "地址ID不能为空")
    private Long addressId;

    /**
     * 用户优惠券ID
     */
    private Long couponId;

    /**
     * 买家订单备注
     */
    @Size(max = 255, message = "订单备注不能超过255个字符")
    private String buyerRemark;

    /**
     * 订单场景
     */
    @NotBlank(message = "订单场景不能为空")
    @EnumValue(enumClass = OrderSceneEnum.class)
    private String scene;
}
