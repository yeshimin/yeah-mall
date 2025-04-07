package com.yeshimin.yeahboot.upms.domain.dto;

import com.yeshimin.yeahboot.upms.common.enums.ResTypeEnum;
import com.yeshimin.yeahboot.upms.common.validation.EnumValue;
import com.yeshimin.yeahboot.upms.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class SysResUpdateDto extends BaseDomain {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    /**
     * 类型：1-菜单 2-页面 3-按钮 4-接口
     */
    @EnumValue(enumClass = ResTypeEnum.class)
    private Integer type;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 名称（父下唯一）
     */
    private String name;

    /**
     * 权限标识（全局唯一）
     */
    private String permission;

    /**
     * 前端路径
     */
    private String path;

    /**
     * 前端组件
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 是否外链
     */
    private Boolean isLink;

    /**
     * 外链地址
     */
    private String linkUrl;

    /**
     * 是否启用：1-是 0-否
     */
    private Boolean enabled;

    /**
     * 是否展示：1-是 0-否
     */
    private Boolean displayed;

    /**
     * 排序：自然数
     */
    private Integer sort;

    /**
     * 备注
     */
    private String remark;
}
