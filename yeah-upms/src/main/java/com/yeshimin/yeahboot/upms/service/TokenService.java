package com.yeshimin.yeahboot.upms.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.yeshimin.yeahboot.common.common.enums.JwtSubjectEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Token相关
 */
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtService jwtService;

    /**
     * 生成token
     */
    public String generateToken(String userId) {
        return jwtService.signJwt(userId, JwtSubjectEnum.DEFAULT.getValue());
    }

    /**
     * 解析token
     */
    public DecodedJWT decodeToken(String token) {
        return jwtService.decodeJwt(token);
    }
}
