package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.DeliveryProviderEntity;
import com.yeshimin.yeahboot.data.mapper.DeliveryProviderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class DeliveryProviderRepo extends BaseRepo<DeliveryProviderMapper, DeliveryProviderEntity> {
}
