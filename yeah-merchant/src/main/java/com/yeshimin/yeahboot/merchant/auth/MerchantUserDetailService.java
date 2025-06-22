package com.yeshimin.yeahboot.merchant.auth;

import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import com.yeshimin.yeahboot.auth.service.UserDetailService;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.merchant.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.merchant.data.repository.MerchantRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantUserDetailService implements UserDetailService {

    private final MerchantRepo merchantRepo;

    @Override
    public UserDetail getUserDetail(String userId) {
        MerchantEntity user = merchantRepo.findOneById(Long.valueOf(userId));
        if (user == null) {
            throw new BaseException(ErrorCodeEnum.FAIL);
        }

        MerchantUserDetail userDetail = new MerchantUserDetail();
        userDetail.setUsername(user.getLoginAccount());
        return userDetail;
    }

    @Override
    public String getSubject() {
        return AuthSubjectEnum.MERCHANT.getValue();
    }
}
