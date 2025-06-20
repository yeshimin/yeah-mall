package com.yeshimin.yeahboot.app.auth;

import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import lombok.Setter;

@Setter
public class AppUserDetail implements UserDetail {

    private String username;

    @Override
    public String getUsername() {
        return this.username;
    }
}
