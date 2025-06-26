package com.yeshimin.yeahboot.auth.service;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yeshimin.yeahboot.auth.domain.vo.JwtPayloadVo;
import com.yeshimin.yeahboot.common.common.consts.CacheKeyConsts;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.common.service.CacheService;
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
//    public String generateToken(String userId) {
//        return jwtService.signJwt(userId, AuthSubjectEnum.DEFAULT.getValue(), null);
//    }

    /**
     * 生成token
     */
    public String generateToken(String userId, String subject, String terminal) {
        return jwtService.signJwt(userId, subject, terminal);
    }

    /**
     * 解析token
     */
    public JwtPayloadVo decodeToken(String token) {
        JwtPayloadVo payloadVo = jwtService.decodePayload(token);
        DecodedJWT decodedJWT = jwtService.decodeJwt(token, payloadVo.getSub(), payloadVo.getTerm());
        return decodedJWT != null ? payloadVo : null;
    }

    /**
     * 缓存token
     */
    public void cacheToken(String subject, String userId, String terminal, String token) {
        // 区分是否有终端
        final String key = StrUtil.isBlank(terminal) ? String.format(CacheKeyConsts.USER_TOKEN, subject, userId) :
                String.format(CacheKeyConsts.USER_TOKEN_TERM, subject, userId, terminal);
        cacheService.set(key, token, CommonConsts.USER_TOKEN_EXPIRE_SECONDS);
    }

    /**
     * 获取缓存的token
     */
    public String getCacheToken(String subject, String userId, String terminal) {
        // 无终端的
        if (StrUtil.isBlank(terminal)) {
            return cacheService.get(String.format(CacheKeyConsts.USER_TOKEN, subject, userId));
        }
        // 有终端的
        return cacheService.get(String.format(CacheKeyConsts.USER_TOKEN_TERM, subject, userId, terminal));
    }
}
