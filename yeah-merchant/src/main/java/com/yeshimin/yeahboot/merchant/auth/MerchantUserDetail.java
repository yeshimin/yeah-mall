package com.yeshimin.yeahboot.merchant.auth;

import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import lombok.Setter;

@Setter
public class MerchantUserDetail implements UserDetail {

    private String username;

    @Override
    public String getUsername() {
        return this.username;
    }
}
