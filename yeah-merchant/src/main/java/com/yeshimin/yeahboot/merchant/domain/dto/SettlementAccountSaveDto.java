package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveData;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveScene;
import com.yeshimin.yeahboot.common.common.sensitive.SensitiveType;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.data.common.enums.SettlementAccountTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 商家结算账户保存参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettlementAccountSaveDto extends ShopDataBaseDomain {

    /**
     * 账户类型：1银行卡，2支付宝
     */
    @NotBlank(message = "结算账户类型不能为空")
    @EnumValue(enumClass = SettlementAccountTypeEnum.class, message = "结算账户类型不正确")
    private String accountType;

    /**
     * 收款人或账户名称
     */
    @NotBlank(message = "收款账户名称不能为空")
    @Size(max = 64, message = "收款账户名称不能超过64个字符")
    @SensitiveData(type = SensitiveType.NAME, scenes = SensitiveScene.LOG)
    private String accountName;

    /**
     * 收款账号
     */
    @NotBlank(message = "收款账号不能为空")
    @Size(max = 128, message = "收款账号不能超过128个字符")
    @SensitiveData(scenes = SensitiveScene.LOG)
    private String accountNo;

    /**
     * 开户银行
     */
    @Size(max = 128, message = "开户银行不能超过128个字符")
    private String bankName;
}
