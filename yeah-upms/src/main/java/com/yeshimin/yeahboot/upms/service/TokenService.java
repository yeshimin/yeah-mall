package com.yeshimin.yeahboot.upms.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.yeshimin.yeahboot.common.common.consts.CacheKeyConsts;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
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
    private final CacheService cacheService;

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

    /**
     * 缓存token
     */
    public void cacheToken(String userId, String token) {
        final String key = String.format(CacheKeyConsts.USER_TOKEN, userId);
        cacheService.set(key, token, CommonConsts.USER_TOKEN_EXPIRE_SECONDS);
    }

    /**
     * 获取缓存的token
     */
    public String getCacheToken(String userId) {
        final String key = String.format(CacheKeyConsts.USER_TOKEN, userId);
        return cacheService.get(key);
    }
}
