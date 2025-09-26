package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.app.domain.dto.UpdateProfileDto;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppMemberService {

    private final MemberRepo memberRepo;

    public MemberEntity detail(Long userId) {
        return memberRepo.findOneById(userId);
    }

    /**
     * 更新个人信息
     */
    public MemberEntity updateProfile(Long userId, UpdateProfileDto dto) {
        MemberEntity member = memberRepo.getOneById(userId);

        // 按需更新
        if (StrUtil.isNotBlank(dto.getNickname())) {
            member.setNickname(dto.getNickname());
        }
        if (dto.getGender() != null) {
            member.setGender(dto.getGender());
        }
        if (dto.getBirthday() != null) {
            member.setBirthday(dto.getBirthday());
        }
        boolean r = member.updateById();
        log.debug("updateProfile.result：{}", r);

        return member;
    }
}
