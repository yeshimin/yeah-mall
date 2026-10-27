package com.yeshimin.yeahboot.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yeshimin.yeahboot.data.domain.vo.AdminMemberVo;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMemberService {

    private final MemberRepo memberRepo;

    /**
     * 分页查询会员管理列表
     */
    public IPage<AdminMemberVo> query(Page<AdminMemberVo> page, Long memberId, String keyword) {
        // 会员资料与积分账户联表返回，零积分会员也应出现在平台会员管理中。
        return memberRepo.queryAdminMembers(page, memberId, keyword == null ? null : keyword.trim());
    }
}
