package com.yeshimin.yeahboot.data.repository;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MemberRepo extends BaseRepo<MemberMapper, MemberEntity> {

    /**
     * findOneByMobile
     */
    public MemberEntity findOneByMobile(String phone) {
        if (StrUtil.isBlank(phone)) {
            throw new IllegalArgumentException("phone不能为空");
        }
        return this.lambdaQuery().eq(MemberEntity::getMobile, phone).one();
    }
}
