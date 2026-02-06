package com.yeshimin.yeahboot.admin.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.admin.domain.dto.PlatBannerCreateDto;
import com.yeshimin.yeahboot.admin.domain.dto.PlatBannerUpdateDto;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.common.service.base.BaseService;
import com.yeshimin.yeahboot.data.domain.entity.PlatBannerEntity;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.repository.PlatBannerRepo;
import com.yeshimin.yeahboot.merchant.service.PermissionService;
import com.yeshimin.yeahboot.storage.StorageManager;
import com.yeshimin.yeahboot.storage.common.properties.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminPlatBannerService extends BaseService {

    private final PermissionService permissionService;

    private final PlatBannerRepo bannerRepo;

    private final StorageProperties storageProperties;
    private final StorageManager storageManager;

    private String bucket;
    private String path;

    @PostConstruct
    public void init() {
        this.bucket = storageProperties.getBiz().getBanner().getBucket();
        this.path = storageProperties.getBiz().getBanner().getPath();
    }

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public PlatBannerEntity create(PlatBannerCreateDto dto, StorageTypeEnum storageType) {
        MultipartFile file = dto.getFile();

        // 决定bucket，除了local存储方式需要使用this.bucket，其他方式都指定为null
        String bucket = storageType == StorageTypeEnum.LOCAL ? this.bucket : null;
        // path用日期
        String path = YsmUtils.dateStr();
        // 存储文件
        SysStorageEntity result = storageManager.put(bucket, path, file, storageType, true, true);
        if (!result.getSuccess()) {
            log.info("result: {}", JSON.toJSONString(result));
            throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
        }

        // 添加Banner记录
        PlatBannerEntity banner = new PlatBannerEntity();
        banner.setImageUrl(result.getFileKey());
        boolean r = banner.insert();
        log.info("banner.create.result: {}", r);

        return banner;
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public PlatBannerEntity update(PlatBannerUpdateDto dto, StorageTypeEnum storageType) {
        // 查询banner
        PlatBannerEntity banner = bannerRepo.getOneById(dto.getId());

        MultipartFile file = dto.getFile();
        if (file != null) {
            // 决定bucket，除了local存储方式需要使用this.bucket，其他方式都指定为null
            String bucket = storageType == StorageTypeEnum.LOCAL ? this.bucket : null;
            // path用日期
            String path = YsmUtils.dateStr();
            // 存储文件
            SysStorageEntity result = storageManager.put(bucket, path, file, storageType, true, true);
            if (!result.getSuccess()) {
                log.info("result: {}", JSON.toJSONString(result));
                throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
            }

            // 按需释放旧图片
            if (StrUtil.isNotBlank(banner.getImageUrl())) {
                storageManager.unmarkUse(banner.getImageUrl());
            }

            // 设置新的值
            banner.setImageUrl(result.getFileKey());
        }

        // 更新Banner记录
        boolean r = banner.updateById();
        log.info("banner.update.result: {}", r);

        return banner;
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Collection<Long> ids) {
        // 查询
        List<PlatBannerEntity> banners = bannerRepo.findListByIds(ids);
        for (PlatBannerEntity banner : banners) {
            // 删除文件
            storageManager.delete(banner.getImageUrl());
        }

        return bannerRepo.removeByIds(ids);
    }
}
