package com.yeshimin.yeahboot.auth.common.config.security;

import com.yeshimin.yeahboot.auth.domain.dto.AuthDto;
import com.yeshimin.yeahboot.auth.domain.vo.AuthVo;
import com.yeshimin.yeahboot.auth.service.AuthService;
import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Jwt Token认证提供器 for ProviderManager
 */
@Slf4j
public class JwtTokenAuthenticationProvider implements AuthenticationProvider {

    private final AuthService authService;

    // constructor
    public JwtTokenAuthenticationProvider(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtTokenAuthenticationToken requestToken = (JwtTokenAuthenticationToken) authentication;
        String token = (String) requestToken.getCredentials();

        AuthDto authDto = new AuthDto();
        authDto.setToken(token);
        AuthVo authVo = authService.auth(authDto);
        if (authVo == null || !authVo.getAuthenticated()) {
            log.error("调用auth服务鉴权失败！");
            throw new BaseException(ErrorCodeEnum.FAIL, "调用auth服务鉴权失败！");
        }

        // set web context
        WebContextUtils.setToken(token);
        WebContextUtils.setUserId(authVo.getUserId());
        WebContextUtils.setSubject(authVo.getSubject());
        WebContextUtils.setTerminal(authVo.getTerminal());
        WebContextUtils.setUsername(authVo.getUsername());
        WebContextUtils.setRoles(authVo.getRoles());
        WebContextUtils.setResources(authVo.getResources());
//        Optional.ofNullable(authVo.getUser()).ifPresent(user -> {
//            WebContextUtils.setUsername(user.getUsername());
//            WebContextUtils.setNickname(user.getNickname());
//        });

        // role
        Set<String> authoritySet = authVo.getRoles().stream().map(s -> "ROLE_" + s).collect(Collectors.toSet());
        // add resource
        authoritySet.addAll(authVo.getResources());
//        // add subject(sub-system) and terminal
//        authoritySet.add(authVo.getSubject());
//        authoritySet.add(authVo.getTerminal());
        String commaSeparatedRoles = String.join(",", authoritySet);

        // set authorities
        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(commaSeparatedRoles);
        // new authentication
        JwtTokenAuthenticationToken resultToken = new JwtTokenAuthenticationToken(authorities);
        resultToken.setAuthenticated(true);

        return resultToken;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }
}