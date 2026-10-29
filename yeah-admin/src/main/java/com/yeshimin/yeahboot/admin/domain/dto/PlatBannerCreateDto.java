package com.yeshimin.yeahboot.admin.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlatBannerCreateDto extends BaseDomain {

    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    /**
     * 跳转目标，为空时不跳转
     */
    @Pattern(regexp = "^$|^/pages/.*", message = "跳转目标必须是/pages/开头的站内路径")
    private String target;
}
