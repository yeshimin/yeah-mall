package com.yeshimin.yeahboot.data.repository;

import com.yeshimin.yeahboot.common.repository.base.BaseRepo;
import com.yeshimin.yeahboot.data.domain.entity.MemberAddressEntity;
import com.yeshimin.yeahboot.data.mapper.MemberAddressMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MemberAddressRepo extends BaseRepo<MemberAddressMapper, MemberAddressEntity> {

    /**
     * countByMemberId
     */
    public Long countByMemberId(Long memberId) {
        return lambdaQuery().eq(MemberAddressEntity::getMemberId, memberId).count();
    }

    /**
     * clearDefault
     */
    public boolean clearDefault(Long memberId) {
        return this.lambdaUpdate()
                .eq(MemberAddressEntity::getMemberId, memberId)
                .set(MemberAddressEntity::getIsDefault, false)
                .update();
    }
}
