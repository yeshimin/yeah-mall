package com.yeshimin.yeahboot.app.auth;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.auth.service.TerminalAndTokenControlService;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.enums.AuthTerminalEnum;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.PasswordService;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 鉴权服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppAuthService {

    //    private final AppUserRepo appUserRepo;
    private final MemberRepo memberRepo;

    private final PasswordService passwordService;
    private final TerminalAndTokenControlService controlService;

    /**
     * 登录
     */
    public LoginVo login(LoginDto loginDto) {
        // 查找用户
        MemberEntity member = memberRepo.findOneByMobile(loginDto.getMobile());
        if (member == null) {
            throw new BaseException(ErrorCodeEnum.FAIL, "用户未找到");
        }

        // 校验密码
        boolean success = passwordService.validatePassword(loginDto.getPassword(), member.getPassword());
        if (!success) {
            throw new BaseException(ErrorCodeEnum.FAIL, "密码不正确");
        }

        String userId = String.valueOf(member.getId());
        String subValue = AuthSubjectEnum.APP.getValue();
        String termValue = StrUtil.isNotBlank(loginDto.getTerminal()) ?
                loginDto.getTerminal() : AuthTerminalEnum.APP.getValue();

        String token = controlService.doControl(userId, subValue, termValue);

        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        return loginVo;
    }
}
