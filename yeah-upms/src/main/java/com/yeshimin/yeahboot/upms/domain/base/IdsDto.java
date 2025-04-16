package com.yeshimin.yeahboot.upms.domain.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class IdsDto extends BaseDomain {

    @NotNull(message = "ID集合不能为空")
    @NotEmpty(message = "ID集合不能为空")
    private List<Long> ids;
}
