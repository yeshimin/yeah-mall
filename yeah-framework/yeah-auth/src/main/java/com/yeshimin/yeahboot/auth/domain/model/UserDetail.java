package com.yeshimin.yeahboot.auth.domain.model;

import java.util.Collections;
import java.util.Set;

/**
 * 鉴权用户详情
 */
public interface UserDetail {

    String getUsername();

    default Set<String> getRoles() {
        return Collections.emptySet();
    }

    default Set<String> getResources() {
        return Collections.emptySet();
    }
}
