package com.yeshimin.yeahboot.data.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yeshimin.yeahboot.data.domain.entity.DeliveryProviderEntity;
import com.yeshimin.yeahboot.data.domain.vo.DeliveryProviderOptionVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 物流服务商 Mapper
 */
@Mapper
public interface DeliveryProviderMapper extends BaseMapper<DeliveryProviderEntity> {

    /**
     * 查询跨店铺去重后的物流服务商下拉选项
     */
    List<DeliveryProviderOptionVo> queryDistinctOptions(@Param("keyword") String keyword);
}
