package com.yeshimin.yeahboot.upms.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.upms.domain.dto.AuthDto;
import com.yeshimin.yeahboot.upms.domain.dto.AuthenticateDto;
import com.yeshimin.yeahboot.upms.domain.dto.LoginDto;
import com.yeshimin.yeahboot.upms.domain.entity.SysUserEntity;
import com.yeshimin.yeahboot.upms.domain.vo.AuthVo;
import com.yeshimin.yeahboot.upms.domain.vo.AuthenticateVo;
import com.yeshimin.yeahboot.upms.domain.vo.LoginVo;
import com.yeshimin.yeahboot.upms.domain.vo.UserRolesAndResourcesVo;
import com.yeshimin.yeahboot.upms.repository.SysUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 鉴权服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserService sysUserService;

    private final SysUserRepo sysUserRepo;

    private final PasswordService passwordService;
    private final TokenService tokenService;

    /**
     * 登录
     */
    public LoginVo login(LoginDto loginDto) {
        AuthenticateDto authenticateDto = new AuthenticateDto();
        authenticateDto.setUsername(loginDto.getUsername());
        authenticateDto.setPassword(loginDto.getPassword());
        AuthenticateVo authenticateVo = this.authenticate(authenticateDto);
        if (!authenticateVo.getSuccess()) {
            throw new BaseException(ErrorCodeEnum.FAIL);
        }

        // 生成token
        String token = tokenService.generateToken(String.valueOf(authenticateVo.getUserId()));

        // 缓存token
        tokenService.cacheToken(String.valueOf(authenticateVo.getUserId()), token);

        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setUsername(loginDto.getUsername());
        return loginVo;
    }

    /**
     * 鉴权（认证和授权）（token方式）
     */
    public AuthVo auth(AuthDto dto) {
        AuthVo authVo = new AuthVo();

        // 认证
        DecodedJWT decodedJWT = tokenService.decodeToken(dto.getToken());
        if (decodedJWT == null) {
            log.error("token decode fail");
            authVo.setAuthenticated(false);
            return authVo;
        } else if (!Objects.equals(dto.getToken(), tokenService.getCacheToken(decodedJWT.getAudience().get(0)))) {
            log.error("token cache validation fail");
            authVo.setAuthenticated(false);
            return authVo;
        } else {
            authVo.setAuthenticated(true);
            authVo.setUserId(Long.valueOf(decodedJWT.getAudience().get(0)));
            authVo.setSubject(decodedJWT.getSubject());
        }

//        // 是否只进行认证
//        if (dto.getOnlyAuthenticate()) {
//            return authVo;
//        }

        Long userId = Long.valueOf(decodedJWT.getAudience().get(0));

        // 授权
        UserRolesAndResourcesVo resultVo = sysUserService.queryUserRolesAndResources(userId);
        if (resultVo == null) {
            throw new BaseException(ErrorCodeEnum.FAIL);
        }

        authVo.setAuthenticated(true);
        authVo.setUser(resultVo.getUser());
        authVo.setRoles(resultVo.getRoles());
        authVo.setResources(resultVo.getResources());
        return authVo;
    }

    // ================================================================================

    /**
     * 认证（账号密码方式）
     */
    private AuthenticateVo authenticate(AuthenticateDto authenticateDto) {
        AuthenticateVo vo = new AuthenticateVo();

        // 查找系统用户
        SysUserEntity sysUser = sysUserRepo.findOneByUsername(authenticateDto.getUsername());
        if (sysUser == null) {
            vo.setSuccess(false);
        } else {
            vo.setUserId(sysUser.getId());
            vo.setUsername(sysUser.getUsername());

            // 校验密码
            boolean success = passwordService.validatePassword(authenticateDto.getPassword(), sysUser.getPassword());
            vo.setSuccess(success);
        }
        return vo;
    }
}
