package com.yeshimin.yeahboot.common.domain.base;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IdNameVo extends BaseDomain {

    /**
     * ID
     */
    private Long id;

    /**
     * 名称
     */
    private String name;
}
