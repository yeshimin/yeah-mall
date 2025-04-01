package com.yeshimin.yeahboot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 密码相关服务
 */
@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    /**
     * 校验密码
     */
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    /**
     * 加密密码
     */
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
}
