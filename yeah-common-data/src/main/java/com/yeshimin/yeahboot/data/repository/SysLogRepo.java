package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.domain.entity.SysLogEntity;
import com.yeshimin.yeahboot.common.mapper.SysLogMapper;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class SysLogRepo extends BaseRepo<SysLogMapper, SysLogEntity> {
}
