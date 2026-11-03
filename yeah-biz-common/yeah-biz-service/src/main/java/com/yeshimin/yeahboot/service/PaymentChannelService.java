package com.yeshimin.yeahboot.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.properties.WxPayProperties;
import com.yeshimin.yeahboot.data.common.enums.PaymentCollectionModeEnum;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.PaymentChannelEntity;
import com.yeshimin.yeahboot.data.repository.PaymentChannelRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 支付通道路由服务
 */
@Service
@RequiredArgsConstructor
public class PaymentChannelService {

    private static final String PLATFORM_TARGET = "platform";

    private final PaymentChannelRepo paymentChannelRepo;
    private final WxPayProperties wxPayProperties;

    /**
     * 为新订单选择支付通道
     */
    public ResolvedPaymentChannel resolveForNewOrder(Long shopId, String appTarget) {
        String normalizedTarget = StrUtil.blankToDefault(StrUtil.trim(appTarget), PLATFORM_TARGET);
        // 店铺专属路由优先，可覆盖平台默认直连通道。
        PaymentChannelEntity route = paymentChannelRepo.findEnabledRoute(shopId, normalizedTarget);
        if (route != null) {
            return this.resolve(route, true);
        }

        if (PLATFORM_TARGET.equalsIgnoreCase(normalizedTarget)) {
            // 平台App未配置店铺专属路由时，统一回退到平台普通商户直连。
            PaymentChannelEntity platformDirect = paymentChannelRepo.findEnabledPlatformChannel(
                    PaymentCollectionModeEnum.PLATFORM_DIRECT.getValue());
            if (platformDirect != null) {
                return this.resolve(platformDirect, true);
            }
            if (paymentChannelRepo.hasPlatformConfiguration()) {
                throw new BaseException("当前店铺尚未配置可用的平台支付通道");
            }
            // 仅数据库尚未启用支付通道管理时兼容旧YML配置。
            return new ResolvedPaymentChannel(wxPayProperties);
        }
        throw new BaseException("当前独立店铺尚未配置可用的微信支付通道");
    }

    /**
     * 按订单支付快照解析通道
     */
    public ResolvedPaymentChannel resolveForOrder(OrderEntity order) {
        // 新订单按通道ID读取不可变历史版本，确保查单和退款仍使用原支付主体。
        if (order.getPaymentChannelId() != null) {
            PaymentChannelEntity route = paymentChannelRepo.findOneById(order.getPaymentChannelId());
            if (route == null) {
                throw new BaseException("订单支付通道不存在");
            }
            return this.resolve(route, false);
        }
        if (StrUtil.isBlank(order.getPaymentChannelCode())
                || ResolvedPaymentChannel.LEGACY_CHANNEL_CODE.equals(order.getPaymentChannelCode())) {
            // 兼容支付通道表上线前创建的历史订单。
            return new ResolvedPaymentChannel(wxPayProperties);
        }
        return this.resolveByChannelCode(order.getPaymentChannelCode());
    }

    /**
     * 按回调通道编码解析通道
     */
    public ResolvedPaymentChannel resolveByChannelCode(String channelCode) {
        if (StrUtil.isBlank(channelCode) || ResolvedPaymentChannel.LEGACY_CHANNEL_CODE.equals(channelCode)) {
            return new ResolvedPaymentChannel(wxPayProperties);
        }
        PaymentChannelEntity route = paymentChannelRepo.findByChannelCode(channelCode);
        if (route == null) {
            throw new BaseException("支付回调通道不存在");
        }
        return this.resolve(route, false);
    }

    /**
     * 查询当前App目标用于微信登录换取身份的应用配置
     */
    public ResolvedPaymentChannel resolveAppCredential(Long shopId, String appTarget) {
        String normalizedTarget = StrUtil.blankToDefault(StrUtil.trim(appTarget), PLATFORM_TARGET);
        if (!PLATFORM_TARGET.equalsIgnoreCase(normalizedTarget)) {
            // 独立店铺App使用其订单通道中的AppID和AppSecret换取OpenID。
            return this.resolveForNewOrder(shopId, normalizedTarget);
        }

        // 平台普通商户和服务商主通道必须使用同一平台AppID，优先复用普通商户配置。
        PaymentChannelEntity direct = paymentChannelRepo.findEnabledPlatformChannel(
                PaymentCollectionModeEnum.PLATFORM_DIRECT.getValue());
        PaymentChannelEntity partner = paymentChannelRepo.findEnabledPlatformChannel(
                PaymentCollectionModeEnum.PLATFORM_PARTNER.getValue());
        PaymentChannelEntity selected = direct != null ? direct : partner;
        if (selected != null) {
            return this.resolve(selected, true);
        }
        if (paymentChannelRepo.hasPlatformConfiguration()) {
            throw new BaseException("平台微信应用通道已停用");
        }
        return new ResolvedPaymentChannel(wxPayProperties);
    }

    /**
     * 解析路由及其实际签名凭据
     */
    private ResolvedPaymentChannel resolve(PaymentChannelEntity route, boolean requireEnabled) {
        if (requireEnabled && !Boolean.TRUE.equals(route.getIsEnabled())) {
            throw new BaseException("支付通道已停用");
        }
        PaymentChannelEntity credential = route;
        if (route.getParentId() != null) {
            // 服务商或平台子路由只保存路由参数，证书和密钥统一取主通道。
            credential = paymentChannelRepo.findOneById(route.getParentId());
            if (credential == null) {
                throw new BaseException("支付主通道不存在");
            }
            if (requireEnabled && !Boolean.TRUE.equals(credential.getIsEnabled())) {
                throw new BaseException("支付主通道已停用");
            }
        }
        this.validateResolved(route, credential);
        return new ResolvedPaymentChannel(route, credential);
    }

    /**
     * 校验解析后的支付凭据和服务商子商户参数
     */
    private void validateResolved(PaymentChannelEntity route, PaymentChannelEntity credential) {
        if (StrUtil.hasBlank(credential.getAppId(), credential.getMchId(), credential.getCertificateSerialNo(),
                credential.getPrivateKeyRaw(), credential.getWechatPayPublicKeyId(),
                credential.getWechatPayPublicKeyRaw(), credential.getApiV3Key(),
                credential.getPayNotifyUrl(), credential.getRefundNotifyUrl())) {
            throw new BaseException("支付通道凭据配置不完整");
        }
        if (PaymentCollectionModeEnum.PLATFORM_PARTNER.getValue().equals(route.getCollectionMode())
                && StrUtil.isBlank(route.getSubMchId())) {
            throw new BaseException("服务商支付路由未配置子商户号");
        }
    }
}
