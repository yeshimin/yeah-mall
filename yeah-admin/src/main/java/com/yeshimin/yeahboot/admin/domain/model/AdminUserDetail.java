package com.yeshimin.yeahboot.admin.domain.model;

import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import lombok.Setter;

import java.util.List;

@Setter
public class AdminUserDetail implements UserDetail {

    private String username;
    private List<String> roles;
    private List<String> resources;

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public List<String> getRoles() {
        return this.roles;
    }

    @Override
    public List<String> getResources() {
        return this.resources;
    }
}
