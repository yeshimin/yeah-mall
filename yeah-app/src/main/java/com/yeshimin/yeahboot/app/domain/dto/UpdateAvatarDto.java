package com.yeshimin.yeahboot.app.domain.dto;

import com.yeshimin.yeahboot.common.domain.base.BaseDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "更新头像DTO")
public class UpdateAvatarDto extends BaseDomain {

    @NotNull(message = "文件不能为空")
    @Schema(description = "文件")
    private MultipartFile file;
}
