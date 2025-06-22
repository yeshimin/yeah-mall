package com.yeshimin.yeahboot.merchant.auth;

import com.yeshimin.yeahboot.auth.service.TokenService;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.enums.AuthTerminalEnum;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.PasswordService;
import com.yeshimin.yeahboot.merchant.data.domain.entity.MerchantEntity;
import com.yeshimin.yeahboot.merchant.data.repository.MerchantRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 鉴权服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantAuthService {

    private final MerchantRepo merchantRepo;

    private final PasswordService passwordService;
    private final TokenService tokenService;

    /**
     * 登录
     */
    public LoginVo login(LoginDto loginDto) {
        // 查找用户
        MerchantEntity user = merchantRepo.findOneByLoginAccount(loginDto.getUsername());
        if (user == null) {
            throw new BaseException(ErrorCodeEnum.FAIL, "用户未找到");
        }

        // 校验密码
        boolean success = passwordService.validatePassword(loginDto.getPassword(), user.getLoginPassword());
        if (!success) {
            throw new BaseException(ErrorCodeEnum.FAIL, "密码不正确");
        }

        // 生成token merchant模块、web端
        String subject = AuthSubjectEnum.MERCHANT.getValue();
        String terminal = AuthTerminalEnum.WEB.getValue();
        String token = tokenService.generateToken(String.valueOf(user.getId()), subject, terminal);

        // 缓存token
        tokenService.cacheToken(subject, String.valueOf(user.getId()), terminal, token);

        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        return loginVo;
    }
}
