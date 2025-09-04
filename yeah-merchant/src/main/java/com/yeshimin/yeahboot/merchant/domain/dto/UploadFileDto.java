package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.common.controller.validation.Update;
import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class UploadFileDto extends ShopDataBaseDomain {

    @NotNull(message = "ID不能为空", groups = {Update.class})
    private Long id;

    @NotNull(message = "文件不能为空")
    private MultipartFile file;
}
