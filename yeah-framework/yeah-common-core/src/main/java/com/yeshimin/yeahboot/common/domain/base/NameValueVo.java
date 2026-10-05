package com.yeshimin.yeahboot.common.domain.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 字符串键值对
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NameValueVo extends BaseDomain {

    /**
     * 名称
     */
    private String name;

    /**
     * 值
     */
    private String value;
}
