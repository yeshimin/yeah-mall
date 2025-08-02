package com.yeshimin.yeahboot.basic.service.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.basic.common.properties.StorageProperties;
import com.yeshimin.yeahboot.basic.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.basic.domain.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.basic.repository.SysStorageRepo;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.InputStream;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "yeah-boot.storage.impl.minio", name = "enabled", havingValue = "true")
public class MinioStorageProvider implements StorageProvider {

    private MinioClient minioClient;

    private final StorageProperties storageProperties;

    private final SysStorageRepo sysStorageRepo;

    private StorageProperties.MinioImpl minio;

    @SneakyThrows
    @PostConstruct
    public void init() {
        this.minio = storageProperties.getImpl().getMinio();

        if (!minio.getEnabled()) {
            log.info("[yeah-boot.storage.impl.minio] is disabled.");
            return;
        }

        minioClient = new MinioClient(
                minio.getEndpoint(), minio.getAccessKey(), minio.getSecretKey());
    }

    @Override
    public SysStorageEntity put(String path, String fileName, Object file) {
        MultipartFile mFile = (MultipartFile) file;

        // 生成fileKey
        String fileKey = IdUtil.simpleUUID();
        @Nullable String suffix = FileUtil.getSuffix(mFile.getOriginalFilename());
        // 最终的文件名（不带路径）
        String finalName = StrUtil.isBlank(suffix) ? fileKey : fileKey + "." + suffix;

        boolean success;
        try {
            minioClient.putObject(minio.getBucketName(), finalName, mFile.getInputStream(),
                    new PutObjectOptions(-1, PutObjectOptions.MIN_MULTIPART_SIZE));
            success = true;
        } catch (Exception e) {
            log.error("MinIO存储失败: {}", e.getMessage(), e);
            success = false;
        }

        SysStorageEntity result = new SysStorageEntity();
        result.setStorageType(StorageTypeEnum.QINIU.getValue());
        result.setSuccess(success);
        result.setBasePath("");
        result.setBucket(minio.getBucketName());
        result.setFileKey(fileKey);
        result.setPath(path);
        result.setSuffix(suffix);
        result.setOriginalName(mFile.getOriginalFilename());

        return result;
    }

    @Override
    public InputStream get(String fileKey, SysStorageEntity sysStorage) {
        final String key = sysStorage.getFileKey() +
                (StrUtil.isBlank(sysStorage.getSuffix()) ? "" : "." + sysStorage.getSuffix());

        try {
            return minioClient.getObject(minio.getBucketName(), key);
        } catch (Exception e) {
            log.error("MinIO获取文件失败: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getDownloadInfo(String fileKey, String fileName, SysStorageEntity sysStorage) {
        throw new UnsupportedOperationException("MinIO存储方式不支持获取下载链接");
    }

    @Override
    public int getPriority() {
        return minio.getPriority();
    }

    @Override
    public StorageTypeEnum getStorageType() {
        return StorageTypeEnum.MINIO;
    }
}
