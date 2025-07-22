package com.yeshimin.yeahboot.auth.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.yeshimin.yeahboot.auth.domain.vo.JwtPayloadVo;
import com.yeshimin.yeahboot.common.common.consts.CacheKeyConsts;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.common.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
    public void cacheToken(String subject, String userId, String terminal, String token, Integer timestampS) {
        // 区分是否有终端
        final String key = //StrUtil.isBlank(terminal) ? String.format(CacheKeyConsts.USER_TOKEN, subject, userId) :
                String.format(CacheKeyConsts.USER_TERMINAL_TOKEN, subject, userId, terminal, timestampS);
        cacheService.set(key, token, CommonConsts.USER_TOKEN_EXPIRE_SECONDS);
    }

    /**
     * 获取缓存的token
     */
    public String getCacheToken(String subject, String userId, String terminal) {
        // 无终端的
//        if (StrUtil.isBlank(terminal)) {
//            return cacheService.get(String.format(CacheKeyConsts.USER_TOKEN, subject, userId));
//        }
        // 有终端的
        return cacheService.get(String.format(CacheKeyConsts.USER_TOKEN_TERM, subject, userId, terminal));
    }

    /**
     * 获取sub下用户所有终端信息
     * hash类型
     */
    public Map<String, String> getSubjectTerminalInfo(String subject, String userId) {
        Map<Object, Object> map =
                cacheService.getHash(String.format(CacheKeyConsts.USER_SUBJECT_TERMINAL_INFO, subject, userId));
        // convert to map<String, String>
        return map.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> entry.getValue().toString()));
    }

    /**
     * 获取终端下token信息
     * hash类型
     */
    public Map<String, String> getTerminalTokenInfo(String subject, String userId, String terminal) {
        Map<Object, Object> map =
                cacheService.getHash(String.format(CacheKeyConsts.USER_TERMINAL_TOKEN_INFO, subject, userId, terminal));
        // convert to map<String, String>
        return map.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> entry.getValue().toString()));
    }

    /**
     * 删除token
     */
    public void deleteToken(String subject, String userId, String terminal) {
        cacheService.delete(// StrUtil.isBlank(terminal) ?
//                String.format(CacheKeyConsts.USER_TOKEN, subject, userId) :
                String.format(CacheKeyConsts.USER_TOKEN_TERM, subject, userId, terminal));
    }

    /**
     * 删除终端下token信息
     */
    public void deleteTerminalTokenInfo(String subject, String userId, String terminal) {
        cacheService.delete(String.format(CacheKeyConsts.USER_TERMINAL_TOKEN_INFO, subject, userId, terminal));
    }

    /**
     * 删除终端和token信息
     */

    /**
     * 删除sub下用户所有终端信息
     */
    public void deleteSubjectTerminalInfo(String subject, String userId) {
        cacheService.delete(String.format(CacheKeyConsts.USER_SUBJECT_TERMINAL_INFO, subject, userId));
    }

    /**
     * 设置sub下用户终端信息
     */
    public void setSubjectTerminalInfo(String subject, String userId, Map<String, String> terminalInfo) {
        String key = String.format(CacheKeyConsts.USER_SUBJECT_TERMINAL_INFO, subject, userId);
        cacheService.setHash(key, terminalInfo);
        cacheService.expire(key, CommonConsts.USER_TOKEN_EXPIRE_SECONDS);
    }

    /**
     * 设置sub下用户终端信息
     */
    public void setSubjectTerminalInfo(String subject, String userId, String terminal, Integer iat, Integer exp) {
        String key = String.format(CacheKeyConsts.USER_SUBJECT_TERMINAL_INFO, subject, userId);
        if (cacheService.exists(key)) {
            cacheService.setHash(key, terminal, iat + "," + exp);
        } else {
            Map<String, String> map = new HashMap<>();
            map.put(terminal, iat + "," + exp);
            cacheService.setHash(key, map);
        }
        cacheService.expire(key, CommonConsts.USER_TOKEN_EXPIRE_SECONDS);
    }

    /**
     * 设置终端下token信息
     */
    public void setTerminalTokenInfo(String subject, String userId, String terminal, Integer iat, Integer exp) {
        String key = String.format(CacheKeyConsts.USER_TERMINAL_TOKEN_INFO, subject, userId, terminal);
        if (cacheService.exists(key)) {
            cacheService.setHash(key, String.valueOf(iat), String.valueOf(exp));
        } else {
            Map<String, String> map = new HashMap<>();
            map.put(String.valueOf(iat), String.valueOf(exp));
            cacheService.setHash(key, map);
        }
        cacheService.expire(key, CommonConsts.USER_TOKEN_EXPIRE_SECONDS);
    }
}
