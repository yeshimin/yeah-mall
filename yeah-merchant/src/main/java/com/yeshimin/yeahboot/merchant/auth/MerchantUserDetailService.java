package com.yeshimin.yeahboot.merchant.auth;

import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import com.yeshimin.yeahboot.auth.service.UserDetailService;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantUserDetailService implements UserDetailService {

//    private final AppUserService UserService;

    @Override
    public UserDetail getUserDetail(String userId) {
//        AppUserEntity user = appUserService.queryByUserId(Long.valueOf(userId));
//        if (user == null) {
//            throw new BaseException(ErrorCodeEnum.FAIL);
//        }

        MerchantUserDetail userDetail = new MerchantUserDetail();
//        userDetail.setUsername(user.getMobile());
        return userDetail;
    }

    @Override
    public String getSubject() {
        return AuthSubjectEnum.MERCHANT.getValue();
    }
}
