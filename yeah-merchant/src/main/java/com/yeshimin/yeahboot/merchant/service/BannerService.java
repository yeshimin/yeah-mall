package com.yeshimin.yeahboot.merchant.service;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.data.domain.entity.BannerEntity;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.repository.BannerRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.BannerCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.BannerUpdateDto;
import com.yeshimin.yeahboot.storage.StorageManager;
import com.yeshimin.yeahboot.storage.common.properties.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class BannerService {

    private final PermissionService permissionService;

    private final BannerRepo bannerRepo;

    private final StorageProperties storageProperties;
    private final StorageManager storageManager;

    private String bucket;
    private String path;

    @PostConstruct
    public void init() {
        this.bucket = storageProperties.getBiz().getFile().getBucket();
        this.path = storageProperties.getBiz().getFile().getPath();
    }

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public BannerEntity create(Long userId, BannerCreateDto dto, StorageTypeEnum storageType) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        MultipartFile file = dto.getFile();

        // 决定bucket，除了local存储方式需要使用this.bucket，其他方式都指定为null
        String bucket = storageType == StorageTypeEnum.LOCAL ? this.bucket : null;
        // path用日期
        String path = YsmUtils.dateStr();
        // 存储文件
        SysStorageEntity result = storageManager.put(bucket, path, file, storageType, true);
        if (!result.getSuccess()) {
            log.info("result: {}", JSON.toJSONString(result));
            throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
        }

        // 添加Banner记录
        BannerEntity banner = new BannerEntity();
        banner.setMchId(dto.getMchId());
        banner.setShopId(dto.getShopId());
        banner.setImageUrl(result.getFileKey());
        boolean r = banner.insert();
        log.info("banner.create.result: {}", r);

        return banner;
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public BannerEntity update(Long userId, BannerUpdateDto dto, StorageTypeEnum storageType) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 查询banner
        BannerEntity banner = bannerRepo.getOneById(dto.getId());

        MultipartFile file = dto.getFile();
        if (file != null) {
            // 决定bucket，除了local存储方式需要使用this.bucket，其他方式都指定为null
            String bucket = storageType == StorageTypeEnum.LOCAL ? this.bucket : null;
            // path用日期
            String path = YsmUtils.dateStr();
            // 存储文件
            SysStorageEntity result = storageManager.put(bucket, path, file, storageType, true);
            if (!result.getSuccess()) {
                log.info("result: {}", JSON.toJSONString(result));
                throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
            }

            // 先删除旧文件
            storageManager.delete(banner.getImageUrl());

            // 设置新的值
            banner.setImageUrl(result.getFileKey());
        }

        // 更新Banner记录
        boolean r = banner.updateById();
        log.info("banner.update.result: {}", r);

        return banner;
    }
}
