package com.yeshimin.yeahboot.app.auth;

import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import com.yeshimin.yeahboot.auth.service.UserDetailService;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserDetailService implements UserDetailService {

    private final MemberRepo memberRepo;

    @Override
    public UserDetail getUserDetail(String userId) {
        MemberEntity member = memberRepo.findOneById(Long.valueOf(userId));
        if (member == null) {
            throw new BaseException(ErrorCodeEnum.FAIL);
        }

        AppUserDetail userDetail = new AppUserDetail();
        userDetail.setUsername(member.getMobile());
        return userDetail;
    }

    @Override
    public String getSubject() {
        return AuthSubjectEnum.APP.getValue();
    }
}
