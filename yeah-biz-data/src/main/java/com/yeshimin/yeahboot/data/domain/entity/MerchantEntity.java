package com.yeshimin.yeahboot.data.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.yeshimin.yeahboot.common.domain.base.ConditionBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商家表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("s_merchant")
public class MerchantEntity extends ConditionBaseEntity<MerchantEntity> {

    /**
     * 登录帐号
     */
    private String loginAccount;

    /**
     * 登录密码
     */
    private String loginPassword;
}
