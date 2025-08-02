package com.yeshimin.yeahboot.basic.controller;

import com.yeshimin.yeahboot.basic.domain.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.basic.domain.vo.FileUploadVo;
import com.yeshimin.yeahboot.basic.service.storage.FileService;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.log.SysLog;
import com.yeshimin.yeahboot.common.domain.base.R;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储
 */
@RestController
@RequestMapping("/basic/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    /**
     * 上传文件
     */
    @SysLog(value = "上传文件", triggerType = "2", category = "4")
    @PostMapping("/upload")
    public R<FileUploadVo> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam(required = false) Integer storageType) {
        // 检查参数：【存储类型】
        StorageTypeEnum storageTypeEnum = null;
        if (storageType != null) {
            storageTypeEnum = StorageTypeEnum.of(storageType);
            if (storageTypeEnum == null) {
                throw new BaseException(ErrorCodeEnum.FAIL, "不支持的存储类型");
            }
        }
        FileUploadVo vo = fileService.upload(file, storageTypeEnum);
        return R.ok(vo);
    }

    /**
     * 下载文件
     */
    @SysLog(value = "下载文件", triggerType = "2", category = "4")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam("fileKey") String fileKey) {
        return fileService.download(fileKey);
    }
}
