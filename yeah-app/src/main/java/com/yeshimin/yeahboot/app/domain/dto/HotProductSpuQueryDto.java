package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.common.config.mybatis.Query;
import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * app端热门商品查询DTO
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Query(custom = true)
public class HotProductSpuQueryDto extends BaseDomain {

    /**
     * 滚动分页token
     */
    private String scrollToken;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
