package com.yeshimin.yeahboot.merchant.domain.dto;

import com.yeshimin.yeahboot.merchant.domain.base.ShopDataBaseDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductSpuMainImageSetDto extends ShopDataBaseDomain {

    @NotNull(message = "文件不能为空")
    private MultipartFile file;

    @NotNull(message = "商品SPU ID不能为空")
    private Long spuId;
}
