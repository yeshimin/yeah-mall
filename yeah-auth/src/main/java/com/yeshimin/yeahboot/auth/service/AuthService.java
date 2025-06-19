package com.yeshimin.yeahboot.auth.service;

import com.yeshimin.yeahboot.auth.domain.dto.AuthDto;
import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import com.yeshimin.yeahboot.auth.domain.vo.AuthVo;
import com.yeshimin.yeahboot.auth.domain.vo.JwtPayloadVo;
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

    private final TokenService tokenService;

    private final UserDetailService userDetailService;

    /**
     * 鉴权（认证和授权）（token方式）
     */
    public AuthVo auth(AuthDto dto) {
        AuthVo authVo = new AuthVo();

        // 认证
        JwtPayloadVo decodedResult = tokenService.decodeToken(dto.getToken());
        if (decodedResult == null) {
            log.error("token decode fail");
            authVo.setAuthenticated(false);
            return authVo;
        } else if (!Objects.equals(dto.getToken(),
                tokenService.getCacheToken(decodedResult.getSub(), decodedResult.getAud(), decodedResult.getTerm()))) {
            log.error("token cache validation fail");
            authVo.setAuthenticated(false);
            return authVo;
        } else {
            authVo.setAuthenticated(true);
            authVo.setUserId(Long.valueOf(decodedResult.getAud()));
            authVo.setSubject(decodedResult.getSub());
            authVo.setTerminal(decodedResult.getTerm());
        }

//        // 是否只进行认证
//        if (dto.getOnlyAuthenticate()) {
//            return authVo;
//        }

        String userId = decodedResult.getAud();

        // 授权
        UserDetail userDetail = userDetailService.getUserDetail(userId);

        authVo.setAuthenticated(true);
        authVo.setUsername(userDetail.getUsername());
        authVo.setRoles(userDetail.getRoles());
        authVo.setResources(userDetail.getResources());
        return authVo;
    }
}
