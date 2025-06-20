package com.yeshimin.yeahboot.auth.service;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson2.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yeshimin.yeahboot.auth.domain.vo.JwtPayloadVo;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.common.common.properties.AuthTokenProperties;
import com.yeshimin.yeahboot.common.common.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Jwt服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;
    private final AuthTokenProperties authTokenProperties;

    // 签发算法
    private Algorithm algorithm;
    // map<subject-terminal, Algorithm>
    private final Map<String, Algorithm> algorithms = new HashMap<>();

    @PostConstruct
    private void init() {
        // 初始化算法 HMAC 默认全局配置
        algorithm = Algorithm.HMAC256(jwtProperties.getSecret());

        // 主题
        if (authTokenProperties.getSubjects() != null) {
            for (AuthTokenProperties.Subject subject : authTokenProperties.getSubjects()) {
                // 终端
                if (subject.getTerminals() != null) {
                    for (AuthTokenProperties.Terminal terminal : subject.getTerminals()) {
                        // 签发算法
                        String key = subject.getName() + "-" + terminal.getName();
                        // 如果subject.jwt未配置，则使用全局jwt配置
                        String secret = subject.getJwt() != null && subject.getJwt().getSecret() != null ?
                                subject.getJwt().getSecret() : jwtProperties.getSecret();
                        algorithms.put(key, Algorithm.HMAC256(secret));
                    }
                }
            }
        }

        log.info("init [JwtService]...algorithms: {}", algorithms);
    }

    // ================================================================================

    /**
     * 签发Jwt
     */
    public String signJwt(String userId, String subject, String terminal) {
        Date now = new Date();
        JWTCreator.Builder builder = JWT.create()
                // 受众（用户标识）
                .withAudience(userId)
                // 签发时间
                .withIssuedAt(now)
                // 主题：区分（子）系统/模块
                .withSubject(subject)
                // 过期时间
                .withExpiresAt(new Date(now.getTime() + jwtProperties.getExpireSeconds() * 1000));

        // 终端
        if (terminal != null) {
            builder.withClaim(CommonConsts.JWT_CLAIM_TERMINAL, terminal);
        }

        // 获取算法
        Algorithm algorithm = algorithms.get(subject + "-" + terminal);
        if (algorithm == null) {
            algorithm = this.algorithm;
        }

        // 签发算法
        return builder.sign(algorithm);
    }

    /**
     * 验证Jwt
     */
//    public boolean validJwt(String jwt) {
//        return validJwt(jwt, null);
//    }

    /**
     * 验证Jwt
     */
//    public boolean validJwt(String jwt, String audience) {
//        DecodedJWT decodedJWT = this.decodeJwt(jwt);
//        if (decodedJWT == null) {
//            return false;
//        }
//
//        // 验证接收人
//        return Objects.equals(audience, decodedJWT.getAudience().get(0));
//    }

    /**
     * 解码Jwt
     */
    public DecodedJWT decodeJwt(String jwt, String subject, String terminal) {
        // 获取算法
        Algorithm algorithm = algorithms.get(subject + "-" + terminal);
        if (algorithm == null) {
            algorithm = this.algorithm;
        }

        JWTVerifier verifier = JWT.require(algorithm)
                // 时间校验偏差
                .acceptLeeway(jwtProperties.getDefaultLeeway())
                .build();

        try {
            return verifier.verify(jwt);
        } catch (JWTDecodeException e) {
            log.error("jwt: {} valid fail", jwt);
        } catch (Exception e) {
            log.error("jwt: {} valid error", jwt);
        }
        return null;
    }

    public JwtPayloadVo decodePayload(String jwt) {
        DecodedJWT decodedJWT = JWT.decode(jwt);

        String payload = Base64.decodeStr(decodedJWT.getPayload());
        JwtPayloadVo payloadVo = JSON.parseObject(payload, JwtPayloadVo.class);
        if (payloadVo == null) {
            throw new IllegalArgumentException("jwt is invalid");
        }
        return payloadVo;
    }
}