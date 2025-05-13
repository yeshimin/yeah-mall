package com.yeshimin.yeahboot.upms.common.config.security;

import com.yeshimin.yeahboot.common.common.enums.ErrorCodeEnum;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import com.yeshimin.yeahboot.common.common.utils.WebContextUtils;
import com.yeshimin.yeahboot.upms.domain.dto.AuthDto;
import com.yeshimin.yeahboot.upms.domain.vo.AuthVo;
import com.yeshimin.yeahboot.upms.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.List;
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

        // role
        List<String> listAuthority = authVo.getRoles().stream().map(s -> "ROLE_" + s).collect(Collectors.toList());
        // add resource
        listAuthority.addAll(authVo.getResources());
        // add subject(sub-system)
        listAuthority.add(authVo.getSubject());
        String commaSeparatedRoles = String.join(",", listAuthority);

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