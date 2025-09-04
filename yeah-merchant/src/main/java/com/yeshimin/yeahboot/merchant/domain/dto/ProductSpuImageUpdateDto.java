package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuImageUpdateDto extends ShopDataBaseDomain {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空")
    private Long id;

    private MultipartFile file;
}
