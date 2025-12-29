package com.yeshimin.yeahboot.app.auth;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.yeshimin.yeahboot.auth.service.TerminalAndTokenControlService;
import com.yeshimin.yeahboot.common.common.consts.CommonConsts;
import com.yeshimin.yeahboot.common.common.enums.AuthSubjectEnum;
import com.yeshimin.yeahboot.common.common.enums.AuthTerminalEnum;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.properties.YeahBootProperties;
import com.yeshimin.yeahboot.common.service.CacheService;
import com.yeshimin.yeahboot.common.service.IdService;
import com.yeshimin.yeahboot.common.service.PasswordService;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import com.yeshimin.yeahboot.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * 鉴权服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppAuthService {

    //    private final AppUserRepo appUserRepo;
    private final MemberRepo memberRepo;

    private final PasswordService passwordService;
    private final TerminalAndTokenControlService controlService;

    private final YeahBootProperties yeahBootProperties;
    private final CacheService cacheService;
    private final IdService idService;
    private final SmsService smsService;

    /**
     * 登录
     */
    @Transactional(rollbackFor = Exception.class)
    public LoginVo login(LoginDto loginDto) {
        // 查找用户
        MemberEntity member = memberRepo.findOneByMobile(loginDto.getMobile());
        if (member == null) {
            // 用户未找到的情况下，如果是短信验证码登录，则直接创建用户
            if (StrUtil.isNotBlank(loginDto.getSmsCode())) {
                if (this.consumeSmsCode(loginDto.getMobile(), loginDto.getSmsCode())) {
                    // 创建用户
                    member = this.createMobileUser(loginDto.getMobile());
                } else {
                    throw new BaseException(ErrorCodeEnum.FAIL, "短信验证码不匹配");
                }
            } else {
                throw new BaseException(ErrorCodeEnum.FAIL, "用户未找到");
            }
        }
        // 用户存在的情况下，直接进行验证
        else {
            // 判断认证方式：短信验证码 或 密码
            if (StrUtil.isNotBlank(loginDto.getSmsCode())) {
                if (!this.consumeSmsCode(loginDto.getMobile(), loginDto.getSmsCode())) {
                    throw new BaseException(ErrorCodeEnum.FAIL, "短信验证码不匹配");
                }
            } else if (StrUtil.isNotBlank(loginDto.getPassword())) {
                if (!passwordService.validatePassword(loginDto.getPassword(), member.getPassword())) {
                    throw new BaseException(ErrorCodeEnum.FAIL, "密码不正确");
                }
            } else {
                throw new BaseException(ErrorCodeEnum.FAIL, "至少选择一种认证方式");
            }
        }

        String userId = String.valueOf(member.getId());
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
        String smsCode = RandomUtil.randomNumbers(yeahBootProperties.getSmsCodeLength());
        // 生成缓存key
        String key = String.format(CommonConsts.APP_SMS_CODE_KEY, dto.getMobile());
        log.debug("smsCode: {}, key: {}", smsCode, key);
        // 执行缓存
        cacheService.set(key, smsCode, yeahBootProperties.getSmsCodeExpSeconds());
        // 发送短信
        SendSmsResponse response = smsService.sendSms(smsCode, dto.getMobile());
        log.info("Response: {}", JSON.toJSONString(response));
    }

    // ================================================================================

    /**
     * 消费短信验证码
     * 如果成功，则删除缓存
     */
    private boolean consumeSmsCode(String mobile, String smsCode) {
        // TODO temp 测试的验证码
        if ("999999".equals(smsCode)) {
            return true;
        }
        String key = String.format(CommonConsts.APP_SMS_CODE_KEY, mobile);
        String code = cacheService.get(key);
        if (StrUtil.isNotBlank(code) && Objects.equals(code, smsCode)) {
            cacheService.delete(key);
            return true;
        }
        return false;
    }

    /**
     * 创建手机用户
     */
    private MemberEntity createMobileUser(String mobile) {
        MemberEntity user = new MemberEntity();
        user.setMobile(mobile);
        user.setAccount(idService.nextEncodedId());
        boolean r = user.insert();
        log.debug("createMobileUser.result: {}, user: {}", r, user);
        return user;
    }
}
