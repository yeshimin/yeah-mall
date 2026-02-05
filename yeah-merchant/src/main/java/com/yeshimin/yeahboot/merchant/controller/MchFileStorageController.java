package com.yeshimin.yeahboot.merchant.controller;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.common.controller.base.BaseController;
import com.yeshimin.yeahboot.common.domain.base.R;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.domain.vo.FileUploadVo;
import com.yeshimin.yeahboot.storage.StorageManager;
import com.yeshimin.yeahboot.storage.common.properties.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 商家端文件存储相关
 */
@Slf4j
@RestController
@RequestMapping("/mch/storage")
@RequiredArgsConstructor
public class MchFileStorageController extends BaseController {

    private final StorageManager storageManager;

    private final StorageProperties storageProperties;

    /**
     * 上传
     */
    @PostMapping("/upload")
    public R<FileUploadVo> upload(MultipartFile file) {
        // 决定bucket，除了local存储方式需要使用this.bucket，其他方式都指定为null
        String bucket = storageProperties.getBiz().getAppCommon().getBucket();
        // path用日期
        String path = YsmUtils.dateStr();
        SysStorageEntity result = storageManager.put(bucket, path, file, null, true, false);
        if (!result.getSuccess()) {
            log.info("result: {}", JSON.toJSONString(result));
            throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
        }

        FileUploadVo vo = new FileUploadVo();
        vo.setFileKey(result.getFileKey());
        return R.ok(vo);
    }
}
