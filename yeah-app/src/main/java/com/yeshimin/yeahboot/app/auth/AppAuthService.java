package com.yeshimin.yeahboot.app.auth;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.auth.service.TerminalAndTokenControlService;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.enums.AuthTerminalEnum;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.properties.YeahBootProperties;
import com.yeshimin.yeahboot.common.service.IdService;
import com.yeshimin.yeahboot.common.service.PasswordService;
import com.yeshimin.yeahboot.data.domain.entity.AppUserEntity;
import com.yeshimin.yeahboot.data.repository.AppUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 鉴权服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppAuthService {

    private final AppUserRepo appUserRepo;

    private final PasswordService passwordService;
    private final TerminalAndTokenControlService controlService;

    private final YeahBootProperties yeahBootProperties;
    private final StringRedisTemplate redisTemplate;
    private final IdService idService;

    /**
     * 登录
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginVo login(LoginDto loginDto) {
        // 查找用户
        AppUserEntity appUser = appUserRepo.findOneByMobile(loginDto.getMobile());
        if (appUser == null) {
            // 用户未找到的情况下，如果是短信验证码登录，则直接创建用户
            if (StrUtil.isNotBlank(loginDto.getSmsCode())) {
                if (Objects.equals(loginDto.getSmsCode(), this.getSmsCode(loginDto.getMobile()))) {
                    // 创建用户
                    appUser = this.createMobileUser(loginDto.getMobile());
                } else {
                    throw new BaseException(ErrorCodeEnum.FAIL, "短信验证码不匹配");
                }
            } else {
                throw new BaseException(ErrorCodeEnum.FAIL, "用户未找到");
            }
        }

        // 判断认证方式：短信验证码 或 密码
        if (StrUtil.isNotBlank(loginDto.getSmsCode())) {
            if (!Objects.equals(loginDto.getSmsCode(), this.getSmsCode(loginDto.getMobile()))) {
                throw new BaseException(ErrorCodeEnum.FAIL, "短信验证码不匹配");
            }
        } else if (StrUtil.isNotBlank(loginDto.getPassword())) {
            if (!passwordService.validatePassword(loginDto.getPassword(), appUser.getPassword())) {
                throw new BaseException(ErrorCodeEnum.FAIL, "密码不正确");
            }
        } else {
            throw new BaseException(ErrorCodeEnum.FAIL, "至少选择一种认证方式");
        }

        String userId = String.valueOf(appUser.getId());
        String subValue = AuthSubjectEnum.APP.getValue();
        String termValue = StrUtil.isNotBlank(loginDto.getTerminal()) ?
                loginDto.getTerminal() : AuthTerminalEnum.APP.getValue();

        String token = controlService.doControl(userId, subValue, termValue);

        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        return loginVo;
    }

    /**
     * 发送短信验证码
     */
    public void sendSmsCode(SendSmsCodeDto dto) {
        // 生成短信验证码
        String smsCode = Arrays.stream(RandomUtil.randomInts(yeahBootProperties.getSmsCodeLength()))
                .mapToObj(String::valueOf)
                .collect(Collectors.joining());
        // 生成缓存key
        String key = String.format(CommonConsts.SMS_CODE_KEY, dto.getMobile());
        log.debug("smsCode: {}, key: {}", smsCode, key);
        // 执行缓存
        redisTemplate.opsForValue().set(key, smsCode, yeahBootProperties.getSmsCodeExpSeconds(), TimeUnit.SECONDS);
    }

    // ================================================================================

    /**
     * 获取短信验证码
     */
    private String getSmsCode(String mobile) {
        return redisTemplate.opsForValue().get(String.format(CommonConsts.SMS_CODE_KEY, mobile));
    }

    /**
     * 创建手机用户
     */
    private AppUserEntity createMobileUser(String mobile) {
        AppUserEntity user = new AppUserEntity();
        user.setMobile(mobile);
        user.setAccount(idService.nextEncodedId());
        boolean r = user.insert();
        log.debug("createMobileUser.result: {}, user: {}", r, user);
        return user;
    }
}
