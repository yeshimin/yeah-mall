package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.app.common.enums.GenderEnum;
import com.yeshimin.yeahboot.common.common.validation.EnumValue;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * 更新个人信息DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "更新个人信息DTO")
public class UpdateProfileDto extends BaseDomain {

    /**
     * 昵称
     */
    @Length(min = 3, max = 16, message = "昵称长度不能超过16个字符")
    @Schema(description = "昵称")
    private String nickname;

    /**
     * 头像
     */
    @Length(max = 255, message = "头像长度不能超过255个字符")
    @Schema(description = "头像")
    private String avatar;

    /**
     * 性别
     */
    @EnumValue(enumClass = GenderEnum.class, message = "性别参数错误")
    @Schema(description = "性别")
    private Integer gender;

    /**
     * 生日
     */
    @Schema(description = "生日")
    private LocalDate birthday;
}
