package com.yeshimin.yeahboot.auth.domain.model;

import java.util.Collections;
import java.util.List;

/**
 * 鉴权用户详情
 */
public interface UserDetail {

    String getUsername();

    default List<String> getRoles() {
        return Collections.emptyList();
    }

    default List<String> getResources() {
        return Collections.emptyList();
    }
}
