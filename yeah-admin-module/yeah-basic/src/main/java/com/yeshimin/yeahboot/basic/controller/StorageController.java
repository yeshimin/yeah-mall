package com.yeshimin.yeahboot.basic.controller;

import com.yeshimin.yeahboot.basic.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.basic.domain.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.basic.domain.vo.FileUploadVo;
import com.yeshimin.yeahboot.basic.mapper.SysStorageMapper;
import com.yeshimin.yeahboot.basic.repository.SysStorageRepo;
import com.yeshimin.yeahboot.basic.service.storage.StorageService;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.controller.base.CrudController;
import com.yeshimin.yeahboot.common.domain.base.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 存储管理
 */
@RestController
@RequestMapping("/basic/storage")
public class StorageController extends CrudController<SysStorageMapper, SysStorageEntity, SysStorageRepo> {

    @Autowired
    private StorageService storageService;

    public StorageController(SysStorageRepo service) {
        // 由于lombok方案无法实现构造方法中调用super，只能显式调用
        super(service);
        super.setModule("basic:storage").disableCreate().disableUpdate().disableDelete();
    }

    /**
     * 上传文件
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':upload')")
    @PostMapping("/upload")
    public R<FileUploadVo> upload(@RequestParam("file") MultipartFile file,
                                  @RequestParam(required = false) Integer storageType,
                                  @RequestParam(required = false) String path,
                                  @RequestParam(required = false, defaultValue = "false") Boolean isPublic) {
        // 检查参数：【存储类型】
        StorageTypeEnum storageTypeEnum = null;
        if (storageType != null) {
            storageTypeEnum = StorageTypeEnum.of(storageType);
            if (storageTypeEnum == null) {
                throw new BaseException(ErrorCodeEnum.FAIL, "不支持的存储类型");
            }
        }
        FileUploadVo vo = storageService.upload(file, storageTypeEnum, path, isPublic);
        return R.ok(vo);
    }

    /**
     * 下载文件
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':download')")
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> download(@RequestParam("fileKey") String fileKey) {
        return storageService.download(fileKey, false);
    }

    /**
     * 公开下载
     */
    @GetMapping("/public")
    public ResponseEntity<InputStreamResource> publicDownload(@RequestParam("fileKey") String fileKey) {
        return storageService.download(fileKey, true);
    }

    /**
     * 删除文件
     */
    @PreAuthorize("@pms.hasPermission(this.getModule() + ':delete')")
    @PostMapping("/delete")
    public R<Void> delete(@RequestParam("fileKey") String fileKey) {
        storageService.delete(fileKey);
        return R.ok();
    }
}
