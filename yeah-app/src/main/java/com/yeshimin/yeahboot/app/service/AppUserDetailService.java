package com.yeshimin.yeahboot.app.service;

import com.yeshimin.yeahboot.app.domain.entity.AppUserEntity;
import com.yeshimin.yeahboot.app.domain.model.AppUserDetail;
import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import com.yeshimin.yeahboot.auth.service.UserDetailService;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailService {

    private final AppUserService appUserService;

    @Override
    public UserDetail getUserDetail(String userId) {
        AppUserEntity user = appUserService.queryByUserId(Long.valueOf(userId));
        if (user == null) {
            throw new BaseException(ErrorCodeEnum.FAIL);
        }

        AppUserDetail userDetail = new AppUserDetail();
        userDetail.setUsername(user.getMobile());
        return userDetail;
    }
}
