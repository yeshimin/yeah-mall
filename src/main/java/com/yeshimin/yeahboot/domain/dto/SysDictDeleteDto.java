package com.yeshimin.yeahboot.domain.dto;

import com.yeshimin.yeahboot.domain.base.IdsDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统字典-创建-DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysDictDeleteDto extends IdsDto {

    /**
     * 是否强制删除
     */
    private Boolean force;
}
