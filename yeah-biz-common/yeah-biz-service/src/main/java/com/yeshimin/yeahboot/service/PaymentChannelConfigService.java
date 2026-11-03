package com.yeshimin.yeahboot.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.service.IdService;
import com.yeshimin.yeahboot.data.common.enums.PaymentCollectionModeEnum;
import com.yeshimin.yeahboot.data.domain.dto.PaymentChannelSaveDto;
import com.yeshimin.yeahboot.data.domain.entity.PaymentChannelEntity;
import com.yeshimin.yeahboot.data.domain.entity.OrderEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.domain.vo.PaymentChannelVo;
import com.yeshimin.yeahboot.data.repository.PaymentChannelRepo;
import com.yeshimin.yeahboot.data.repository.OrderRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 支付通道配置服务
 */
@Service
@RequiredArgsConstructor
public class PaymentChannelConfigService {

    private final PaymentChannelRepo paymentChannelRepo;
    private final ShopRepo shopRepo;
    private final OrderRepo orderRepo;
    private final IdService idService;

    /**
     * 查询全部支付通道
     */
    public List<PaymentChannelVo> queryAll() {
        List<PaymentChannelEntity> channels = paymentChannelRepo.findAll();
        // 批量补充店铺名称，避免列表转换时逐条查询店铺。
        Map<Long, ShopEntity> shopMap = shopRepo.findListByIds(channels.stream()
                        .map(PaymentChannelEntity::getShopId)
                        .filter(id -> id != null && id > 0)
                        .collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(ShopEntity::getId, Function.identity()));
        return channels.stream().map(entity -> this.toVo(entity, shopMap.get(entity.getShopId())))
                .collect(Collectors.toList());
    }

    /**
     * 查询支付通道详情
     */
    public PaymentChannelVo detail(Long id) {
        PaymentChannelEntity entity = paymentChannelRepo.getOneById(id, "支付通道不存在");
        ShopEntity shop = entity.getShopId() != null && entity.getShopId() > 0
                ? shopRepo.findOneById(entity.getShopId()) : null;
        return this.toVo(entity, shop);
    }

    /**
     * 查询店铺独立App直连通道
     */
    public PaymentChannelVo detailShopDirect(Long shopId, String appTarget) {
        List<PaymentChannelEntity> entities = paymentChannelRepo.lambdaQuery()
                .eq(PaymentChannelEntity::getShopId, shopId)
                .eq(PaymentChannelEntity::getAppTarget, appTarget)
                .eq(PaymentChannelEntity::getCollectionMode, PaymentCollectionModeEnum.SHOP_DIRECT.getValue())
                .orderByDesc(PaymentChannelEntity::getIsEnabled)
                .orderByDesc(PaymentChannelEntity::getUpdateTime)
                .list();
        PaymentChannelEntity entity = entities.isEmpty() ? null : entities.get(0);
        return entity == null ? null : this.toVo(entity, shopRepo.findOneById(shopId));
    }

    /**
     * 保存支付通道
     */
    @Transactional(rollbackFor = Exception.class)
    public PaymentChannelEntity save(PaymentChannelSaveDto dto) {
        // 更新时保留变更前快照，用于判断是否需要生成不可变的历史通道版本。
        PaymentChannelEntity entity = dto.getId() == null
                ? new PaymentChannelEntity() : paymentChannelRepo.getOneById(dto.getId(), "支付通道不存在");
        boolean create = entity.getId() == null;
        PaymentChannelEntity before = create ? null : BeanUtil.copyProperties(entity, PaymentChannelEntity.class);

        String oldAppSecret = entity.getAppSecret();
        String oldSubAppSecret = entity.getSubAppSecret();
        String oldPrivateKey = entity.getPrivateKeyRaw();
        String oldPublicKey = entity.getWechatPayPublicKeyRaw();
        String oldApiV3Key = entity.getApiV3Key();
        Boolean oldEnabled = entity.getIsEnabled();
        BeanUtil.copyProperties(dto, entity);
        entity.setAppTarget(StrUtil.trim(entity.getAppTarget()));
        if (entity.getIsEnabled() == null) {
            entity.setIsEnabled(oldEnabled == null || oldEnabled);
        }
        // 前端不会回显密钥；编辑时敏感字段留空表示继续使用原值。
        this.restoreSecretIfBlank(entity, oldAppSecret, oldSubAppSecret, oldPrivateKey, oldPublicKey, oldApiV3Key);

        this.validateExistingMasterShape(before, entity);
        this.normalizeAndValidate(entity);
        // 已被订单引用的通道不能原地切换支付主体，否则历史订单将无法查单和退款。
        boolean createVersion = !create && this.hasRouteIdentityChanged(before, entity)
                && this.hasReferencedOrders(before);
        if (createVersion) {
            // 停用旧版本并新增通道版本；历史订单仍通过旧ID使用旧凭据。
            before.setIsEnabled(false);
            before.updateById();
            this.clearBaseFields(entity);
            entity.setChannelCode(idService.nextEncodedId());
            entity.insert();
            if (this.isPlatformMaster(before)) {
                // 主通道换版本时，启用中的子路由也必须保留旧版并迁移到新主通道。
                this.migrateEnabledChildren(before.getId(), entity.getId(), Boolean.TRUE.equals(entity.getIsEnabled()));
            }
            return entity;
        }

        // 未被订单引用的通道可原地更新；平台主通道停用时同步停用子路由。
        if (create) {
            entity.setChannelCode(idService.nextEncodedId());
            entity.insert();
        } else {
            entity.updateById();
            if (this.isPlatformMaster(before)
                    && Boolean.TRUE.equals(before.getIsEnabled())
                    && !Boolean.TRUE.equals(entity.getIsEnabled())) {
                this.disableEnabledChildren(entity.getId());
            }
        }
        return entity;
    }

    /**
     * 规范化通道字段并按收款模式校验配置
     */
    private void normalizeAndValidate(PaymentChannelEntity entity) {
        // 所有通道共用的基础字段校验。
        if (StrUtil.hasBlank(entity.getChannelName(), entity.getAppTarget(), entity.getCollectionMode())) {
            throw new BaseException("通道名称、App构建目标和支付收款模式不能为空");
        }
        if (Arrays.stream(PaymentCollectionModeEnum.values())
                .noneMatch(item -> item.getValue().equals(entity.getCollectionMode()))) {
            throw new BaseException("支付收款模式不正确");
        }
        if (entity.getShopId() == null || entity.getShopId() < 0) {
            throw new BaseException("店铺ID不正确");
        }
        ShopEntity shop = entity.getShopId() > 0
                ? shopRepo.getOneById(entity.getShopId(), "店铺不存在") : null;
        boolean platformMaster = entity.getShopId() == 0 && entity.getParentId() == null;
        if (platformMaster) {
            // 平台主通道直接保存完整微信凭据，不允许携带子商户字段。
            entity.setAppTarget("platform");
            this.clearSubMerchantFields(entity);
            if (PaymentCollectionModeEnum.SHOP_DIRECT.getValue().equals(entity.getCollectionMode())) {
                throw new BaseException("平台主通道不能使用独立店铺直连模式");
            }
            if (Boolean.TRUE.equals(entity.getIsEnabled())
                    && paymentChannelRepo.countPlatformMaster(entity.getCollectionMode(), entity.getId()) > 0) {
                throw new BaseException("同类型平台主通道已存在");
            }
            this.validateCredential(entity);
            String otherMode = PaymentCollectionModeEnum.PLATFORM_DIRECT.getValue().equals(entity.getCollectionMode())
                    ? PaymentCollectionModeEnum.PLATFORM_PARTNER.getValue()
                    : PaymentCollectionModeEnum.PLATFORM_DIRECT.getValue();
            PaymentChannelEntity other = paymentChannelRepo.findEnabledPlatformChannel(otherMode);
            if (Boolean.TRUE.equals(entity.getIsEnabled()) && other != null
                    && !other.getId().equals(entity.getId())
                    && !entity.getAppId().equals(other.getAppId())) {
                throw new BaseException("平台普通商户与服务商主通道必须使用同一个平台AppID");
            }
            return;
        }

        if (entity.getShopId() == 0) {
            throw new BaseException("非主通道必须关联具体店铺");
        }
        if (!"platform".equalsIgnoreCase(entity.getAppTarget())
                && !shop.getShopNo().equalsIgnoreCase(entity.getAppTarget())) {
            throw new BaseException("App构建目标必须是platform或当前店铺编号");
        }

        if (Boolean.TRUE.equals(entity.getIsEnabled())
                && paymentChannelRepo.countRoute(entity.getShopId(), entity.getAppTarget(), entity.getId()) > 0) {
            throw new BaseException("该店铺在当前App目标下已配置支付通道");
        }
        if (PaymentCollectionModeEnum.PLATFORM_PARTNER.getValue().equals(entity.getCollectionMode())) {
            // 服务商路由只保存子商户信息，签名与回调凭据继承服务商主通道。
            if (entity.getParentId() == null || StrUtil.isBlank(entity.getSubMchId())) {
                throw new BaseException("服务商子商户路由必须配置服务商主通道和子商户号");
            }
            if ("platform".equalsIgnoreCase(entity.getAppTarget()) && StrUtil.isNotBlank(entity.getSubAppId())) {
                throw new BaseException("平台App服务商路由应使用服务商AppID，不能配置子商户AppID");
            }
            if (!"platform".equalsIgnoreCase(entity.getAppTarget())
                    && StrUtil.hasBlank(entity.getSubAppId(), entity.getSubAppSecret())) {
                throw new BaseException("独立店铺App服务商路由必须配置子商户AppID和AppSecret");
            }
            PaymentChannelEntity parent = paymentChannelRepo.getOneById(entity.getParentId(), "服务商主通道不存在");
            if (!PaymentCollectionModeEnum.PLATFORM_PARTNER.getValue().equals(parent.getCollectionMode())
                    || parent.getParentId() != null) {
                throw new BaseException("父通道不是服务商主通道");
            }
            if (Boolean.TRUE.equals(entity.getIsEnabled()) && !Boolean.TRUE.equals(parent.getIsEnabled())) {
                throw new BaseException("服务商主通道已停用，不能启用子商户路由");
            }
            return;
        }
        if (PaymentCollectionModeEnum.SHOP_DIRECT.getValue().equals(entity.getCollectionMode())) {
            // 独立店铺直连使用店铺自身微信配置，不允许关联平台主通道。
            entity.setParentId(null);
            entity.setAppTarget(shop.getShopNo());
            this.clearSubMerchantFields(entity);
            this.validateCredential(entity);
            return;
        }
        if (entity.getParentId() == null) {
            throw new BaseException("店铺平台直连路由必须关联平台普通商户主通道");
        }
        // 平台普通商户路由只负责指定适用店铺，支付凭据统一继承平台普通商户主通道。
        PaymentChannelEntity parent = paymentChannelRepo.getOneById(
                entity.getParentId(), "平台普通商户主通道不存在");
        if (!PaymentCollectionModeEnum.PLATFORM_DIRECT.getValue().equals(parent.getCollectionMode())
                || parent.getShopId() != 0 || parent.getParentId() != null) {
            throw new BaseException("父通道不是平台普通商户主通道");
        }
        if (Boolean.TRUE.equals(entity.getIsEnabled()) && !Boolean.TRUE.equals(parent.getIsEnabled())) {
            throw new BaseException("平台普通商户主通道已停用，不能启用店铺路由");
        }
    }

    /**
     * 校验直连通道或平台主通道的完整微信凭据
     */
    private void validateCredential(PaymentChannelEntity entity) {
        if (StrUtil.hasBlank(entity.getAppId(), entity.getAppSecret(), entity.getMchId(),
                entity.getCertificateSerialNo(), entity.getPrivateKeyRaw(),
                entity.getWechatPayPublicKeyId(), entity.getWechatPayPublicKeyRaw(),
                entity.getApiV3Key(), entity.getPayNotifyUrl(), entity.getRefundNotifyUrl())) {
            throw new BaseException("支付通道凭据配置不完整");
        }
    }

    /**
     * 编辑时恢复未重新填写的敏感字段
     */
    private void restoreSecretIfBlank(PaymentChannelEntity entity, String appSecret, String subAppSecret,
                                      String privateKey, String publicKey, String apiV3Key) {
        if (StrUtil.isBlank(entity.getAppSecret())) entity.setAppSecret(appSecret);
        if (StrUtil.isBlank(entity.getSubAppSecret())) entity.setSubAppSecret(subAppSecret);
        if (StrUtil.isBlank(entity.getPrivateKeyRaw())) entity.setPrivateKeyRaw(privateKey);
        if (StrUtil.isBlank(entity.getWechatPayPublicKeyRaw())) entity.setWechatPayPublicKeyRaw(publicKey);
        if (StrUtil.isBlank(entity.getApiV3Key())) entity.setApiV3Key(apiV3Key);
    }

    /**
     * 判断影响订单支付主体或回调路由的身份字段是否变化
     */
    private boolean hasRouteIdentityChanged(PaymentChannelEntity before, PaymentChannelEntity after) {
        return !Objects.equals(before.getShopId(), after.getShopId())
                || !Objects.equals(before.getAppTarget(), after.getAppTarget())
                || !Objects.equals(before.getCollectionMode(), after.getCollectionMode())
                || !Objects.equals(before.getParentId(), after.getParentId())
                || !Objects.equals(before.getAppId(), after.getAppId())
                || !Objects.equals(before.getMchId(), after.getMchId())
                || !Objects.equals(before.getSubAppId(), after.getSubAppId())
                || !Objects.equals(before.getSubMchId(), after.getSubMchId());
    }

    /**
     * 限制平台主通道原地修改范围或收款模式
     */
    private void validateExistingMasterShape(PaymentChannelEntity before, PaymentChannelEntity after) {
        if (before == null || !this.isPlatformMaster(before)) {
            return;
        }
        if (!Objects.equals(before.getShopId(), after.getShopId())
                || !Objects.equals(before.getParentId(), after.getParentId())
                || !Objects.equals(before.getCollectionMode(), after.getCollectionMode())) {
            throw new BaseException("平台主通道的范围和收款模式不可修改，请新建对应通道");
        }
    }

    /**
     * 判断通道自身或其子路由是否已被订单引用
     */
    private boolean hasReferencedOrders(PaymentChannelEntity channel) {
        Set<Long> channelIds = paymentChannelRepo.findChildren(channel.getId()).stream()
                .map(PaymentChannelEntity::getId)
                .collect(Collectors.toSet());
        channelIds.add(channel.getId());
        return orderRepo.lambdaQuery().in(OrderEntity::getPaymentChannelId, channelIds).count() > 0;
    }

    /**
     * 将启用中的子路由迁移到新主通道版本
     */
    private void migrateEnabledChildren(Long oldParentId, Long newParentId, boolean enableNewRoutes) {
        List<PaymentChannelEntity> children = paymentChannelRepo.findChildren(oldParentId).stream()
                .filter(child -> Boolean.TRUE.equals(child.getIsEnabled()))
                .collect(Collectors.toList());
        for (PaymentChannelEntity child : children) {
            PaymentChannelEntity next = BeanUtil.copyProperties(child, PaymentChannelEntity.class);
            // 旧路由保留给历史订单，新订单使用复制出的新路由。
            child.setIsEnabled(false);
            child.updateById();
            if (!enableNewRoutes) {
                continue;
            }
            this.clearBaseFields(next);
            next.setParentId(newParentId);
            next.setChannelCode(idService.nextEncodedId());
            next.setIsEnabled(true);
            next.insert();
        }
    }

    /**
     * 停用主通道下所有启用中的子路由
     */
    private void disableEnabledChildren(Long parentId) {
        paymentChannelRepo.findChildren(parentId).stream()
                .filter(child -> Boolean.TRUE.equals(child.getIsEnabled()))
                .forEach(child -> {
                    child.setIsEnabled(false);
                    child.updateById();
                });
    }

    /**
     * 判断是否为平台主通道
     */
    private boolean isPlatformMaster(PaymentChannelEntity entity) {
        return entity != null && Objects.equals(entity.getShopId(), 0L) && entity.getParentId() == null;
    }

    /**
     * 清空复制实体的基础审计字段以生成新版本
     */
    private void clearBaseFields(PaymentChannelEntity entity) {
        entity.setId(null);
        entity.setDeleteTime(null);
        entity.setDeleted(null);
        entity.setCreateTime(null);
        entity.setCreateBy(null);
        entity.setUpdateTime(null);
        entity.setUpdateBy(null);
    }

    /**
     * 清理非服务商路由不应保留的子商户字段
     */
    private void clearSubMerchantFields(PaymentChannelEntity entity) {
        entity.setSubAppId("");
        entity.setSubAppSecret("");
        entity.setSubMchId("");
    }

    /**
     * 转换支付通道视图并仅返回敏感字段配置状态
     */
    private PaymentChannelVo toVo(PaymentChannelEntity entity, ShopEntity shop) {
        PaymentChannelVo vo = BeanUtil.copyProperties(entity, PaymentChannelVo.class);
        vo.setShopName(shop == null ? "平台" : shop.getShopName());
        vo.setHasAppSecret(StrUtil.isNotBlank(entity.getAppSecret()));
        vo.setHasSubAppSecret(StrUtil.isNotBlank(entity.getSubAppSecret()));
        vo.setHasPrivateKey(StrUtil.isNotBlank(entity.getPrivateKeyRaw()));
        vo.setHasWechatPayPublicKey(StrUtil.isNotBlank(entity.getWechatPayPublicKeyRaw()));
        vo.setHasApiV3Key(StrUtil.isNotBlank(entity.getApiV3Key()));
        return vo;
    }
}
