package com.yeshimin.yeahboot.admin.auth;

import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import lombok.Setter;

import java.util.Set;

@Setter
public class AdminUserDetail implements UserDetail {

    private String username;
    private Set<String> roles;
    private Set<String> resources;

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    public Set<String> getResources() {
        return this.resources;
    }
}
