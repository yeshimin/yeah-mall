package com.yeshimin.yeahboot.basic.service.storage;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.basic.common.properties.StorageProperties;
import com.yeshimin.yeahboot.basic.domain.entity.SysFileEntity;
import com.yeshimin.yeahboot.basic.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.basic.domain.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.basic.domain.vo.FileUploadVo;
import com.yeshimin.yeahboot.basic.repository.SysFileRepo;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URLEncoder;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final SysFileRepo sysFileRepo;

    private final StorageManager storageManager;

    private final StorageProperties storageProperties;

    private String bucket;
    private String path;

    @PostConstruct
    public void init() {
        this.bucket = storageProperties.getBiz().getFile().getBucket();
        this.path = storageProperties.getBiz().getFile().getPath();
    }

    /**
     * 上传文件
     */
    @Transactional(rollbackFor = Exception.class)
    public FileUploadVo upload(MultipartFile file, StorageTypeEnum storageType) {
        // 存储文件
        SysStorageEntity result = storageManager.put(this.bucket, this.path, file, storageType);
        if (!result.getSuccess()) {
            log.info("result: {}", JSON.toJSONString(result));
            throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
        }

        // 添加文件记录
        SysFileEntity sysFile = new SysFileEntity();
        sysFile.setStorageType(StorageTypeEnum.LOCAL.getValue());
        sysFile.setBasePath(result.getBasePath());
        sysFile.setBucket(result.getBucket());
        sysFile.setPath(result.getPath());
        sysFile.setFileKey(result.getFileKey());
        sysFile.setSuffix(result.getSuffix());
        sysFile.setOriginalName(result.getOriginalName());
        boolean r = sysFile.insert();
        log.info("sysFile.save.result: {}", r);

        FileUploadVo vo = new FileUploadVo();
        vo.setFileKey(result.getFileKey());
        return vo;
    }

    /**
     * 下载
     */
    public ResponseEntity<InputStreamResource> download(String fileKey) {
        SysFileEntity sysFile = this.getLocalFileByFileKey(fileKey);
        InputStream inputStream = storageManager.get(fileKey);
        return this.wrap(sysFile.getOriginalName(), inputStream);
    }

    /**
     * 删除文件
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String fileKey) {
        SysFileEntity sysFile = this.getLocalFileByFileKey(fileKey);
        sysFile.deleteById();

        // 删除存储
        storageManager.delete(fileKey);
    }

    // ================================================================================

    private SysFileEntity getLocalFileByFileKey(String fileKey) {
        SysFileEntity sysFile = sysFileRepo.findOneByFileKey(fileKey);
        if (sysFile == null) {
            throw new BaseException(ErrorCodeEnum.FAIL, "File not found: " + fileKey);
        }
        return sysFile;
    }

    @SneakyThrows
    private ResponseEntity<InputStreamResource> wrap(String filename, InputStream inputStream) {
        // 设置响应头
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        // 使用 RFC 6266 编码文件名
        String encodedFilename = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");
        headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFilename);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(inputStream));
    }
}
