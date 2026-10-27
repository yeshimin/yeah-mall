package com.yeshimin.yeahboot.data.domain.vo;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 平台会员管理视图
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdminMemberVo extends BaseDomain {

    /**
     * 会员ID
     */
    private Long id;

    /**
     * 登录账号
     */
    private String account;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像文件Key
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 生日
     */
    private LocalDate birthday;

    /**
     * 可用积分
     */
    private Long pointsBalance;

    /**
     * 累计获得积分
     */
    private Long totalPointsEarned;

    /**
     * 累计使用积分
     */
    private Long totalPointsUsed;

    /**
     * 注册时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
