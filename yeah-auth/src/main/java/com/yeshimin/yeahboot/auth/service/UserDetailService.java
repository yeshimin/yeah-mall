package com.yeshimin.yeahboot.auth.service;

import com.yeshimin.yeahboot.auth.domain.model.UserDetail;

/**
 * 鉴权用户详情服务
 */
public interface UserDetailService {

    UserDetail getUserDetail(String userId);

    String getSubject();
}
