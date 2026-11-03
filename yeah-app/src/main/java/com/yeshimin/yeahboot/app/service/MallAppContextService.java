package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.bean.BeanUtil;
import com.yeshimin.yeahboot.app.common.enums.MallAppModeEnum;
import com.yeshimin.yeahboot.app.domain.vo.MallAppContextVo;
import com.yeshimin.yeahboot.app.domain.vo.MallShopBannerVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.data.domain.entity.ShopEntity;
import com.yeshimin.yeahboot.data.repository.BannerRepo;
import com.yeshimin.yeahboot.data.repository.ShopRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

/**
 * 商城App上下文服务
 */
@Service
@RequiredArgsConstructor
public class MallAppContextService {

    public static final String HEADER_APP_TARGET = "X-Mall-App-Target";

    private static final String TARGET_PLATFORM = "platform";
    private static final String REQUEST_ATTRIBUTE = MallAppContextService.class.getName() + ".CONTEXT";
    private static final Pattern SHOP_NO_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{1,32}$");

    private final HttpServletRequest request;
    private final ShopRepo shopRepo;
    private final BannerRepo bannerRepo;

    /**
     * 获取当前商城App上下文
     */
    public MallAppContextVo getContext() {
        Object cached = request.getAttribute(REQUEST_ATTRIBUTE);
        if (cached instanceof MallAppContextVo) {
            return (MallAppContextVo) cached;
        }

        // App构建目标由统一请求头传入；缺省值保持平台App兼容行为。
        String appTarget = this.normalizeTarget(request.getHeader(HEADER_APP_TARGET));
        MallAppContextVo context;
        if (TARGET_PLATFORM.equalsIgnoreCase(appTarget)) {
            context = this.createPlatformContext();
        } else if (!SHOP_NO_PATTERN.matcher(appTarget).matches()) {
            context = this.createUnavailableShopContext(appTarget, "独立店铺编号格式不正确");
        } else {
            context = this.createShopContext(appTarget);
        }
        request.setAttribute(REQUEST_ATTRIBUTE, context);
        return context;
    }

    /**
     * 判断当前是否独立店铺模式
     */
    public boolean isShopMode() {
        return MallAppModeEnum.SHOP.equalsValue(this.getContext().getAppMode());
    }

    /**
     * 获取当前独立店铺ID
     */
    public Long getShopId() {
        MallAppContextVo context = this.getContext();
        if (MallAppModeEnum.SHOP.equalsValue(context.getAppMode())
                && !Boolean.TRUE.equals(context.getAvailable())) {
            throw new BaseException(context.getUnavailableReason());
        }
        return context.getShopId();
    }

    /**
     * 获取当前App构建目标
     */
    public String getAppTarget() {
        return this.getContext().getAppTarget();
    }

    /**
     * 校验数据是否属于当前独立店铺
     */
    public void checkShopScope(Long shopId) {
        if (this.isShopMode() && !this.getShopId().equals(shopId)) {
            throw new BaseException("当前商城不可访问该店铺数据");
        }
    }

    /**
     * 校验店铺是否允许在App展示
     */
    public void checkShopVisible(Long shopId) {
        this.checkShopScope(shopId);
        // 平台停用属于强制隐藏；商家暂停营业不影响浏览。
        ShopEntity shop = shopRepo.getOneById(shopId, "店铺不存在");
        if (!Boolean.TRUE.equals(shop.getIsEnabled())) {
            throw new BaseException("店铺已被平台停用");
        }
    }

    /**
     * 校验店铺当前是否允许交易
     */
    public void checkShopTradeAvailable(Long shopId) {
        this.checkShopVisible(shopId);
        // 交易同时受平台启用状态和商家营业状态约束。
        ShopEntity shop = shopRepo.getOneById(shopId, "店铺不存在");
        if (!Boolean.TRUE.equals(shop.getIsOpen())) {
            throw new BaseException("店铺暂停营业");
        }
    }

    /**
     * 查询当前App允许展示的店铺ID
     */
    public Set<Long> queryVisibleShopIds() {
        if (this.isShopMode()) {
            // 单店App只展示构建时指定的店铺。
            return Collections.singleton(this.getShopId());
        }
        // 平台App仅过滤平台停用店铺，商家休息中的店铺仍允许浏览。
        return shopRepo.lambdaQuery()
                .select(ShopEntity::getId)
                .eq(ShopEntity::getIsEnabled, true)
                .list().stream().map(ShopEntity::getId).collect(Collectors.toSet());
    }

    /**
     * 查询当前独立店铺Banner
     */
    public List<MallShopBannerVo> queryShopBanners() {
        if (!this.isShopMode()) {
            return Collections.emptyList();
        }
        return bannerRepo.lambdaQuery()
                .eq(BannerEntity::getShopId, this.getShopId())
                .orderByAsc(BannerEntity::getId)
                .list().stream()
                .map(entity -> BeanUtil.copyProperties(entity, MallShopBannerVo.class))
                .collect(Collectors.toList());
    }

    /**
     * 创建平台App上下文
     */
    private MallAppContextVo createPlatformContext() {
        MallAppContextVo context = new MallAppContextVo();
        context.setAvailable(true);
        context.setAppMode(MallAppModeEnum.PLATFORM.getValue());
        context.setAppTarget(TARGET_PLATFORM);
        return context;
    }

    /**
     * 按店铺编号创建独立店铺App上下文
     */
    private MallAppContextVo createShopContext(String shopNo) {
        ShopEntity shop = shopRepo.findOneByShopNo(shopNo);
        if (shop == null) {
            return this.createUnavailableShopContext(shopNo, "独立店铺配置不存在或已失效");
        }
        if (!Boolean.TRUE.equals(shop.getIsEnabled())) {
            return this.createUnavailableShopContext(shopNo, "店铺已被平台停用");
        }
        // 商家暂停营业时仍返回可用上下文，让用户浏览并看到暂停营业提示。
        MallAppContextVo context = new MallAppContextVo();
        context.setAvailable(true);
        context.setAppMode(MallAppModeEnum.SHOP.getValue());
        context.setAppTarget(shop.getShopNo());
        context.setShopId(shop.getId());
        context.setShopNo(shop.getShopNo());
        context.setShopName(shop.getShopName());
        context.setShopLogo(shop.getShopLogo());
        context.setShopOpen(Boolean.TRUE.equals(shop.getIsOpen()));
        return context;
    }

    /**
     * 创建不可用的独立店铺App上下文
     */
    private MallAppContextVo createUnavailableShopContext(String shopNo, String reason) {
        MallAppContextVo context = new MallAppContextVo();
        context.setAvailable(false);
        context.setUnavailableReason(reason);
        context.setAppMode(MallAppModeEnum.SHOP.getValue());
        context.setAppTarget(shopNo);
        context.setShopNo(shopNo);
        return context;
    }

    /**
     * 规范化App构建目标
     */
    private String normalizeTarget(String target) {
        if (target == null || target.trim().isEmpty()) {
            return TARGET_PLATFORM;
        }
        return target.trim();
    }
}
