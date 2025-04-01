package com.yeshimin.yeahboot.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yeshimin.yeahboot.common.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Objects;

/**
 * Jwt服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties jwtProperties;

    // 签发算法
    private Algorithm algorithm;

    @PostConstruct
    private void init() {
        log.info("init()");

        log.info("jwtSecret: {}", "******");
        log.info("expireSeconds: {}", jwtProperties.getExpireSeconds());
        log.info("defaultLeeway: {}", jwtProperties.getDefaultLeeway());

        // 初始化算法 HMAC
        algorithm = Algorithm.HMAC256(jwtProperties.getSecret());
    }

    // ================================================================================

    /**
     * 签发Jwt
     */
    public String signJwt(String userId, String subject) {
        Date now = new Date();
        return JWT.create()
                // 受众（用户标识）
                .withAudience(userId)
                // 签发时间
                .withIssuedAt(now)
                // 主题：区分（子）系统
                .withSubject(subject)
                // 过期时间
                .withExpiresAt(new Date(now.getTime() + jwtProperties.getExpireSeconds() * 1000))
                // 签发算法
                .sign(algorithm);
    }

    /**
     * 验证Jwt
     */
    public boolean validJwt(String jwt) {
        return validJwt(jwt, null);
    }

    /**
     * 验证Jwt
     */
    public boolean validJwt(String jwt, String audience) {
        DecodedJWT decodedJWT = this.decodeJwt(jwt);
        if (decodedJWT == null) {
            return false;
        }

        // 验证接收人
        return Objects.equals(audience, decodedJWT.getAudience().get(0));
    }


    /**
     * 解码Jwt
     */
    public DecodedJWT decodeJwt(String jwt) {
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
}