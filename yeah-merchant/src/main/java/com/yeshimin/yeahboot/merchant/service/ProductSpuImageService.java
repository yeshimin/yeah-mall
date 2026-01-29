package com.yeshimin.yeahboot.merchant.service;

import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.common.service.base.BaseService;
import com.yeshimin.yeahboot.data.domain.entity.ProductSpuImageEntity;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.repository.ProductSpuImageRepo;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSpuImageCreateDto;
import com.yeshimin.yeahboot.merchant.domain.dto.ProductSpuImageUpdateDto;
import com.yeshimin.yeahboot.storage.StorageManager;
import com.yeshimin.yeahboot.storage.common.properties.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSpuImageService extends BaseService {

    private final PermissionService permissionService;

    private final ProductSpuImageRepo productSpuImageRepo;

    private final StorageProperties storageProperties;
    private final StorageManager storageManager;

    private String bucket;
    private String path;

    @PostConstruct
    public void init() {
        this.bucket = storageProperties.getBiz().getProduct().getBucket();
        this.path = storageProperties.getBiz().getProduct().getPath();
    }

    /**
     * 创建
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSpuImageEntity create(Long userId, ProductSpuImageCreateDto dto, StorageTypeEnum storageType) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);
        permissionService.checkSpu(dto.getShopId(), dto.getSpuId());

        MultipartFile file = dto.getFile();

        // 决定bucket，除了local存储方式需要使用this.bucket，其他方式都指定为null
        String bucket = storageType == StorageTypeEnum.LOCAL ? this.bucket : null;
        // path用日期
        String path = YsmUtils.dateStr();
        // 存储文件
        SysStorageEntity result = storageManager.put(bucket, path, file, storageType, true, true);
        if (!result.getSuccess()) {
            log.info("result: {}", JSON.toJSONString(result));
            throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
        }

        // 添加记录
        ProductSpuImageEntity entity = new ProductSpuImageEntity();
        entity.setMchId(dto.getMchId());
        entity.setShopId(dto.getShopId());
        entity.setSpuId(dto.getSpuId());
        entity.setImageUrl(result.getFileKey());
        boolean r = entity.insert();
        log.info("productSpuImage.create.result: {}", r);

        return entity;
    }

    /**
     * 更新
     */
    @Transactional(rollbackFor = Exception.class)
    public ProductSpuImageEntity update(Long userId, ProductSpuImageUpdateDto dto, StorageTypeEnum storageType) {
        // 权限检查和控制
        permissionService.checkMchAndShop(userId, dto);

        // 查询
        ProductSpuImageEntity entity = productSpuImageRepo.getOneById(dto.getId());

        MultipartFile file = dto.getFile();
        if (file != null) {
            // 决定bucket，除了local存储方式需要使用this.bucket，其他方式都指定为null
            String bucket = storageType == StorageTypeEnum.LOCAL ? this.bucket : null;
            // path用日期
            String path = YsmUtils.dateStr();
            // 存储文件
            SysStorageEntity result = storageManager.put(bucket, path, file, storageType, true, true);
            if (!result.getSuccess()) {
                log.info("result: {}", JSON.toJSONString(result));
                throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
            }

            // 先删除旧文件
            storageManager.delete(entity.getImageUrl());

            // 设置新的值
            entity.setImageUrl(result.getFileKey());
        }

        // 更新记录
        boolean r = entity.updateById();
        log.info("productSpuImage.update.result: {}", r);

        return entity;
    }

    /**
     * 删除
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean delete(Long userId, Collection<Long> ids) {
        // 批量检查：数据权限
        if (productSpuImageRepo.countByIdsAndNotMchId(userId, ids) > 0) {
            throw new BaseException("包含无权限数据");
        }

        // 查询
        List<ProductSpuImageEntity> listEntity = productSpuImageRepo.findListByIds(ids);
        for (ProductSpuImageEntity entity : listEntity) {
            // 删除文件
            storageManager.delete(entity.getImageUrl());
        }

        return productSpuImageRepo.removeByIds(ids);
    }
}
