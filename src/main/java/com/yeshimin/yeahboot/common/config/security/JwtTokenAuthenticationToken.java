package com.yeshimin.yeahboot.common.config.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.security.auth.Subject;
import java.util.Collection;

/**
 * Jwt Token认证实体 for JwtTokenAuthenticationProvider
 */
public class JwtTokenAuthenticationToken extends AbstractAuthenticationToken {

    private String token;

    public JwtTokenAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    public JwtTokenAuthenticationToken(String token) {
        super(null);
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean implies(Subject subject) {
        return false;
    }
}