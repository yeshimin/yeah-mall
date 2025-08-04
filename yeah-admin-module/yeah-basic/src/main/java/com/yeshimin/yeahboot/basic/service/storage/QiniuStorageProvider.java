package com.yeshimin.yeahboot.basic.service.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.DownloadUrl;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.yeshimin.yeahboot.basic.common.properties.StorageProperties;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.data.repository.SysStorageRepo;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 七牛相关服务
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QiniuStorageProvider implements StorageProvider {

    private static final long UPLOAD_TOKEN_DEFAULT_EXPIRE_SECONDS = 60;
    private static final long DOWNLOAD_TOKEN_DEFAULT_EXPIRE_SECONDS = 3600;

    private final StorageProperties storageProperties;

    private final SysStorageRepo sysStorageRepo;

    private Auth auth;
    private UploadManager uploadManager;
    private BucketManager bucketManager;

    private StorageProperties.QiniuImpl qiniu;

    @PostConstruct
    public void init() {
        this.qiniu = storageProperties.getImpl().getQiniu();

        if (!qiniu.getEnabled()) {
            log.info("[yeah-boot.storage.impl.qiniu] is disabled.");
            return;
        }

        Configuration cfg = new Configuration();

        auth = Auth.create(qiniu.getAccessKey(), qiniu.getSecretKey());
        uploadManager = new UploadManager(cfg);
        bucketManager = new BucketManager(auth, cfg);
    }

    @Override
    public SysStorageEntity put(@Nullable String bucket, @Nullable String path, Object file, boolean isPublic) {

        MultipartFile mFile = (MultipartFile) file;

        // 生成fileKey
        String fileKey = IdUtil.simpleUUID();
        @Nullable String suffix = FileUtil.getSuffix(mFile.getOriginalFilename());
        // 最终的fileKey
        String finalKey = getKeyWithSuffix(fileKey, suffix);
        finalKey = YsmUtils.path(path, finalKey);
        log.info("finalKey: {}", finalKey);

        String finalBucket = isPublic ? qiniu.getPublicBucket() : StrUtil.isBlank(bucket) ? qiniu.getBucket() : bucket;
        final String upToken = this.getToken(finalBucket);
        log.info("upToken: {}", upToken);

        boolean success;
        Response response = null;
        try {
            response = uploadManager.put(mFile.getBytes(), finalKey, upToken);
            success = true;
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage(), e);
            success = false;
        }
        log.info("qiniu.success: {}, response: {}", success, response);

        SysStorageEntity result = new SysStorageEntity();
        result.setStorageType(StorageTypeEnum.QINIU.getValue());
        result.setSuccess(success);
        result.setBasePath("");
        result.setBucket(finalBucket);
        result.setPath(path);
        result.setFileKey(fileKey);
        result.setSuffix(suffix);
        result.setOriginalName(mFile.getOriginalFilename());
        result.setIsPublic(isPublic);

        return result;
    }

    @Override
    public InputStream get(String fileKey, SysStorageEntity sysStorage) {
        String url = this.getDownloadInfo(fileKey, null, sysStorage);
        File temp = FileUtil.createTempFile();
        HttpUtil.downloadFileFromUrl(url, temp);
        try {
            return new TempFileInputStream(temp);
        } catch (FileNotFoundException e) {
            log.error("Failed to create temp file: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void delete(String fileKey, SysStorageEntity sysStorage) {
        final String key = getKeyWithSuffix(fileKey, sysStorage.getSuffix());

        try {
            bucketManager.delete(sysStorage.getBucket(), key);
        } catch (Exception e) {
            log.error("Qiniu删除文件失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getDownloadInfo(String fileKey, String fileName, SysStorageEntity sysStorage) {
        String key = getKeyWithSuffix(fileKey, sysStorage.getSuffix());
        String finalKey = YsmUtils.path(sysStorage.getPath(), key);
        return this.getDownloadUrlByDefault(finalKey, fileName, sysStorage.getIsPublic());
    }

    @Override
    public int getPriority() {
        return qiniu.getPriority();
    }

    @Override
    public StorageTypeEnum getStorageType() {
        return StorageTypeEnum.QINIU;
    }

    // ================================================================================

    /**
     * 获取下载URL
     */
    private String getDownloadUrlByDefault(String key, String fileName, boolean isPublic) {
        String domain = isPublic ? qiniu.getPublicDomain() : qiniu.getDomain();
        DownloadUrl url = new DownloadUrl(domain, false, key);
        // 指定文件名
        if (StrUtil.isNotBlank(fileName)) {
            url.setAttname(fileName);
        }
        try {
            return url.buildURL(auth, this.getExpireSeconds());
        } catch (QiniuException e) {
            throw new RuntimeException(e);
        }
    }

    private int getExpireSeconds() {
        return (int) (System.currentTimeMillis() / 1000 + DOWNLOAD_TOKEN_DEFAULT_EXPIRE_SECONDS);
    }

    /**
     * 获取上传凭证
     */
    private String getToken(String bucketName) {
        StringMap putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
        return auth.uploadToken(bucketName, null, UPLOAD_TOKEN_DEFAULT_EXPIRE_SECONDS, putPolicy);
    }
}
