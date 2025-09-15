package com.yeshimin.yeahboot.app.service;

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
}
