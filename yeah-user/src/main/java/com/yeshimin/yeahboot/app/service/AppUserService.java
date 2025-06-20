package com.yeshimin.yeahboot.app.service;

import com.yeshimin.yeahboot.app.domain.entity.AppUserEntity;
import com.yeshimin.yeahboot.app.repository.AppUserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepo appUserRepo;

    public AppUserEntity queryByUserId(Long userId) {
        return appUserRepo.findOneById(userId);
    }
}
