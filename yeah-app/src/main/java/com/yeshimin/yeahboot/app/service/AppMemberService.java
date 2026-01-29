package com.yeshimin.yeahboot.app.service;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.yeshimin.yeahboot.app.domain.dto.UpdateAvatarDto;
import com.yeshimin.yeahboot.app.domain.dto.UpdatePasswordDto;
import com.yeshimin.yeahboot.app.domain.dto.UpdateProfileDto;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.enums.StorageTypeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.YsmUtils;
import com.yeshimin.yeahboot.common.service.PasswordService;
import com.yeshimin.yeahboot.data.domain.entity.MemberEntity;
import com.yeshimin.yeahboot.data.domain.entity.SysStorageEntity;
import com.yeshimin.yeahboot.data.repository.MemberRepo;
import com.yeshimin.yeahboot.storage.StorageManager;
import com.yeshimin.yeahboot.storage.common.properties.StorageProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppMemberService {

    private final MemberRepo memberRepo;

    private final PasswordService passwordService;

    private final StorageProperties storageProperties;
    private final StorageManager storageManager;

    private String bucket;
    private String path;

    @PostConstruct
    public void init() {
        this.bucket = storageProperties.getBiz().getAvatar().getBucket();
        this.path = storageProperties.getBiz().getAvatar().getPath();
    }

    public MemberEntity detail(Long userId) {
        return memberRepo.findOneById(userId);
    }

    /**
     * 更新个人信息
     */
    @Transactional(rollbackFor = Exception.class)
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

    /**
     * 更新头像
     */
    @Transactional(rollbackFor = Exception.class)
    public MemberEntity updateAvatar(Long userId, UpdateAvatarDto dto) {
        MemberEntity member = memberRepo.getOneById(userId);

        MultipartFile file = dto.getFile();

        // 此处存储类型固定为本地存储
        StorageTypeEnum storageType = StorageTypeEnum.LOCAL;
        // 决定bucket，除了local存储方式需要使用this.bucket，其他方式都指定为null
        String bucket = this.bucket;
        // path用日期
        String path = YsmUtils.dateStr();
        // 存储文件
        SysStorageEntity result = storageManager.put(bucket, path, file, storageType, true, true);
        if (!result.getSuccess()) {
            log.info("result: {}", JSON.toJSONString(result));
            throw new BaseException(ErrorCodeEnum.FAIL, "文件存储失败");
        }

        member.setAvatar(result.getFileKey());
        boolean r = member.updateById();
        log.debug("updateAvatar.result：{}", r);
        return member;
    }

    /**
     * 修改密码
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePassword(Long userId, UpdatePasswordDto dto) {
        MemberEntity member = memberRepo.getOneById(userId);

        String encodePassword = passwordService.encodePassword(dto.getPassword());
        member.setPassword(encodePassword);
        boolean r = member.updateById();
        log.debug("updatePassword.result：{}", r);
    }
}
