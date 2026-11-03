package com.yeshimin.yeahboot.app.auth;

import cn.hutool.core.net.url.UrlBuilder;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson2.JSONObject;
import com.yeshimin.yeahboot.app.service.MallAppContextService;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.MemberWxIdentityEntity;
import com.yeshimin.yeahboot.data.repository.MemberWxIdentityRepo;
import com.yeshimin.yeahboot.service.PaymentChannelService;
import com.yeshimin.yeahboot.service.ResolvedPaymentChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * App微信身份服务
 */
@Service
@RequiredArgsConstructor
public class AppWxIdentityService {

    private static final String CODE_TO_SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private final MallAppContextService mallAppContextService;
    private final PaymentChannelService paymentChannelService;
    private final MemberWxIdentityRepo memberWxIdentityRepo;

    /**
     * 将当前小程序微信身份绑定到手机号会员账号
     */
    @Transactional(rollbackFor = Exception.class)
    public void bind(Long memberId, String code) {
        // 当前App目标决定换取OpenID所使用的AppID，手机号会员仍是唯一主账号。
        Long shopId = mallAppContextService.isShopMode() ? mallAppContextService.getShopId() : 0L;
        ResolvedPaymentChannel channel = paymentChannelService.resolveAppCredential(
                shopId, mallAppContextService.getAppTarget());
        if (StrUtil.hasBlank(channel.getAppId(), channel.getAppSecret())) {
            throw new BaseException("当前小程序尚未配置微信登录参数");
        }

        JSONObject session = this.codeToSession(channel.getAppId(), channel.getAppSecret(), code);
        String openId = session.getString("openid");
        if (StrUtil.isBlank(openId)) {
            throw new BaseException("微信身份获取失败");
        }
        String unionId = session.getString("unionid");

        // 同一AppID下的OpenID只能绑定一个手机号会员账号。
        MemberWxIdentityEntity occupied = memberWxIdentityRepo.findByAppIdAndOpenId(channel.getAppId(), openId);
        if (occupied != null && !Objects.equals(occupied.getMemberId(), memberId)) {
            throw new BaseException("该微信身份已关联其他手机号账号");
        }

        MemberWxIdentityEntity current = memberWxIdentityRepo.findByMemberIdAndAppId(memberId, channel.getAppId());
        if (current != null) {
            if (!Objects.equals(current.getOpenId(), openId)) {
                throw new BaseException("当前手机号已关联该小程序中的其他微信身份");
            }
            if (StrUtil.isNotBlank(unionId) && !Objects.equals(current.getUnionId(), unionId)) {
                current.setUnionId(unionId);
                current.updateById();
            }
            return;
        }

        // 会员可分别绑定平台和多个独立店铺小程序，因此唯一范围是会员与AppID。
        MemberWxIdentityEntity identity = new MemberWxIdentityEntity();
        identity.setMemberId(memberId);
        identity.setAppId(channel.getAppId());
        identity.setOpenId(openId);
        identity.setUnionId(unionId);
        try {
            identity.insert();
        } catch (DuplicateKeyException e) {
            // 唯一索引兜底并发绑定；同一会员的重复请求按幂等成功处理。
            MemberWxIdentityEntity concurrent = memberWxIdentityRepo.findByAppIdAndOpenId(
                    channel.getAppId(), openId);
            if (concurrent != null && Objects.equals(concurrent.getMemberId(), memberId)) {
                return;
            }
            MemberWxIdentityEntity memberIdentity = memberWxIdentityRepo.findByMemberIdAndAppId(
                    memberId, channel.getAppId());
            if (memberIdentity != null) {
                throw new BaseException("当前手机号已关联该小程序中的其他微信身份");
            }
            throw new BaseException("该微信身份已关联其他手机号账号");
        }
    }

    /**
     * 使用微信临时登录凭证换取当前AppID下的微信身份
     */
    private JSONObject codeToSession(String appId, String appSecret, String code) {
        try {
            String url = UrlBuilder.of(CODE_TO_SESSION_URL, StandardCharsets.UTF_8)
                    .addQuery("appid", appId)
                    .addQuery("secret", appSecret)
                    .addQuery("js_code", code)
                    .addQuery("grant_type", "authorization_code")
                    .build();
            String body = HttpRequest.get(url).timeout(10_000).execute().body();
            JSONObject result = JSONObject.parseObject(body);
            Integer errCode = result.getInteger("errcode");
            if (errCode != null && errCode != 0) {
                throw new BaseException("微信登录凭证无效或已过期");
            }
            return result;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            throw new BaseException("微信身份服务暂时不可用");
        }
    }
}
