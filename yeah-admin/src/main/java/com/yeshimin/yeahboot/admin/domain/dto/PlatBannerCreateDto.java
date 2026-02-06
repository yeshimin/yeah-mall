package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatBannerCreateDto extends BaseDomain {

    @NotNull(message = "文件不能为空")
    private MultipartFile file;
}
