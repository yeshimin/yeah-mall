package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.Collection;

/**
 * 线下结算预览参数
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SettlementPreviewDto extends BaseDomain {

    /**
     * 选择的台账ID集合
     */
    @NotEmpty(message = "请选择待结算记录")
    private Collection<Long> ids;
}
