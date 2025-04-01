package com.yeshimin.yeahboot.domain.base;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class BasePageDto<T> extends Page<T> implements Serializable {

    private static final long serialVersionUID = 1L;
}
