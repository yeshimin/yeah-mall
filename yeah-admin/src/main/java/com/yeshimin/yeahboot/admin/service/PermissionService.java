package com.yeshimin.yeahboot.admin.service;

import org.springframework.stereotype.Service;

@Service("pms")
public class PermissionService {

    public boolean hasPermission(String... permission) {
        return true;
    }
}
