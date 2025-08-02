package com.yeshimin.yeahboot.basic.service.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.basic.common.properties.StorageProperties;
import com.yeshimin.yeahboot.basic.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.basic.domain.enums.StorageTypeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "yeah-boot.storage.impl.local", name = "enabled", havingValue = "true")
public class LocalStorageProvider implements StorageProvider {

    // bucketName 命名只能包含字母、数字、下划线、中划线，且必须以字母开头，长度为3-63个字符
    private static final String BUCKET_NAME_REGEX = "^[a-zA-Z0-9][a-zA-Z0-9_-]{2,62}$";
    // path 命名只能包含字母、数字、下划线、中划线，且必须以字母开头，长度为1-63个字符
    private static final String PATH_REGEX = "^[a-zA-Z0-9][a-zA-Z0-9_-]{0,62}$";

    private final StorageProperties storageProperties;

    private StorageProperties.LocalImpl local;

    @PostConstruct
    public void init() {
        this.local = storageProperties.getImpl().getLocal();
    }

    @Override
    public SysStorageEntity put(String bucketName, @Nullable String path, Object file) {
        if (!ReUtil.isMatch(BUCKET_NAME_REGEX, bucketName)) {
            throw new IllegalArgumentException("BucketName format error");
        }
        if (StrUtil.isNotBlank(path) && !ReUtil.isMatch(PATH_REGEX, path)) {
            throw new IllegalArgumentException("Path format error");
        }
        if (!(file instanceof MultipartFile)) {
            throw new IllegalArgumentException("File must be an instance of MultipartFile");
        }

        MultipartFile mFile = (MultipartFile) file;

        // 生成fileKey、获取文件后缀名
        String fileKey = IdUtil.simpleUUID();
        @Nullable String suffix = FileUtil.getSuffix(mFile.getOriginalFilename());
        // 最终的文件名（不带路径）
        String finalName = StrUtil.isBlank(suffix) ? fileKey : fileKey + "." + suffix;

        // 按需创建目录
        File directory = this.makeDirectory(local.getBasePath(), bucketName, path);

        // 生成最终的文件（名）并保存
        File finalFile = FileUtil.file(directory, finalName);
        log.info("local.finalFile: {}", finalFile.getAbsolutePath());

        boolean success;
        try {
            mFile.transferTo(finalFile);
            success = true;
        } catch (IOException e) {
            log.error("Failed to upload file: {}", e.getMessage(), e);
            success = false;
        }
        log.info("local.success: {}", success);

        SysStorageEntity result = new SysStorageEntity();
        result.setStorageType(StorageTypeEnum.LOCAL.getValue());
        result.setSuccess(success);
        result.setBasePath(local.getBasePath());
        result.setBucket(bucketName);
        result.setFileKey(fileKey);
        result.setPath(path);
        result.setSuffix(suffix);
        result.setOriginalName(mFile.getOriginalFilename());

        return result;
    }

    @Override
    public InputStream get(String fileKey, SysStorageEntity sysStorage) {
        try {
            return Files.newInputStream(Paths.get(this.getFullPath(sysStorage)));
        } catch (IOException e) {
            log.error("本地存储获取文件失败: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void delete(String fileKey, SysStorageEntity sysStorage) {
        FileUtil.del(this.getFullPath(sysStorage));
    }

    @Override
    public String getDownloadInfo(String fileKey, String fileName, SysStorageEntity sysStorage) {
        throw new UnsupportedOperationException("本地存储方式不支持获取下载链接");
    }

    @Override
    public int getPriority() {
        return local.getPriority();
    }

    @Override
    public StorageTypeEnum getStorageType() {
        return StorageTypeEnum.LOCAL;
    }

    // ================================================================================

    /**
     * make directory
     */
    public File makeDirectory(String basePath, String bucketName, String path) {
        File directory = FileUtil.file(basePath, bucketName, path);
        if (!FileUtil.exist(directory)) {
            directory = FileUtil.mkdir(directory);
        }
        return directory;
    }

    /**
     * getFullPath
     */
    public String getFullPath(SysStorageEntity sysStorage) {
        String path = FileUtil.file(sysStorage.getBasePath(), sysStorage.getBucket(), sysStorage.getPath(),
                sysStorage.getFileKey() + "." + sysStorage.getSuffix()).getAbsolutePath();
        log.info("path: {}", path);
        return path;
    }
}
