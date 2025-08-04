package com.yeshimin.yeahboot.basic.service.storage;

import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.data.repository.SysStorageRepo;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 存储管理器
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageManager {

    private final SysStorageRepo sysStorageRepo;

    private final List<StorageProvider> storageProviders;

    private Map<StorageTypeEnum, StorageProvider> mapStorageProvider;

    @PostConstruct
    public void init() {
        log.info("storageProviders.size: {}", storageProviders.size());

        if (storageProviders.isEmpty()) {
            throw new IllegalArgumentException("No storage providers found");
        }

        // sort by priority
        storageProviders.sort(Comparator.comparing(StorageProvider::getPriority));

        // list to map
        mapStorageProvider = storageProviders.stream()
                .collect(Collectors.toMap(StorageProvider::getStorageType, provider -> provider));
    }

    public SysStorageEntity put(@Nullable String bucketName, @Nullable String path, Object file,
                                @Nullable StorageTypeEnum storageType) {
        return this.put(bucketName, path, file, storageType, false);
    }

    public SysStorageEntity put(@Nullable String bucketName, @Nullable String path, Object file,
                                @Nullable StorageTypeEnum storageType, boolean isPublic) {
        SysStorageEntity entity = this.getProvider(storageType).put(bucketName, path, file, isPublic);
        entity.setIsPublic(isPublic);
        boolean r = entity.insert();
        log.info("StorageManager.put.result: {}", r);
        return entity;
    }

    public InputStream get(String fileKey) {
        SysStorageEntity sysStorage = sysStorageRepo.getOneByFileKey(fileKey);
        return this.get(fileKey, sysStorage);
    }

    public InputStream get(String fileKey, SysStorageEntity sysStorage) {
        StorageTypeEnum storageType = StorageTypeEnum.of(sysStorage.getStorageType());
        InputStream inputStream = this.getProvider(storageType).get(fileKey, sysStorage);
        if (inputStream == null) {
            throw new BaseException(ErrorCodeEnum.FAIL, "获取文件失败");
        }
        return inputStream;
    }

    /**
     * 删除
     */
    public void delete(String fileKey) {
        SysStorageEntity sysStorage = sysStorageRepo.findOneByFileKey(fileKey);
        if (sysStorage == null) {
            return;
        }
        sysStorage.deleteById();

        StorageTypeEnum storageType = StorageTypeEnum.of(sysStorage.getStorageType());
        this.getProvider(storageType).delete(fileKey, sysStorage);
    }

    public String getDownloadInfo(String fileKey, String fileName) {
        SysStorageEntity sysStorage = sysStorageRepo.getOneByFileKey(fileKey);
        return this.getProvider().getDownloadInfo(fileKey, fileName, sysStorage);
    }

    // ================================================================================

    private StorageProvider getProvider() {
        return this.getProvider(null);
    }

    private StorageProvider getProvider(StorageTypeEnum storageType) {
        if (storageType != null) {
            StorageProvider provider = mapStorageProvider.get(storageType);
            if (provider == null) {
                throw new BaseException(ErrorCodeEnum.FAIL, "不支持的存储类型");
            }
            return provider;
        }
        return storageProviders.get(0);
    }
}
