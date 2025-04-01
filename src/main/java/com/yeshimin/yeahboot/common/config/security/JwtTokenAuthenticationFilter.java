package com.yeshimin.yeahboot.common.config.security;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.yeshimin.yeahboot.common.consts.Common;
import com.yeshimin.yeahboot.common.utils.WebContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Jwt Token认证过滤器 for Spring Security
 */
@Slf4j
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {

    private AuthenticationManager authenticationManager;

    public JwtTokenAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        logger.debug("doFilterInternal()");

        // If required, authenticate and set web context
        authenticateIfRequired(request);

        filterChain.doFilter(request, response);

        // Clear web context
        WebContextUtils.clear();
    }

    /**
     * Authenticate if required
     */
    private void authenticateIfRequired(HttpServletRequest request) {
        String token = this.extractToken(request);
        if (StringUtils.isBlank(token)) {
            log.debug("no token, as anonymous in later AnonymousAuthenticationFilter");
//            // 在过滤链AnonymousAuthenticationFilter中设置为匿名用户
//            // 这里要先clear，否则在匿名Filter中会存在JwtTokenAuthenticationToken；具体原因暂时未知
            SecurityContextHolder.clearContext();
            return;
        }

        // wrap request authentication
        Authentication authRequest = new JwtTokenAuthenticationToken(token);
        // authenticate for result (delegate by ProviderManager)
        Authentication authResult = authenticationManager.authenticate(authRequest);
        // set to security context
        SecurityContextHolder.getContext().setAuthentication(authResult);
    }

    /**
     * Extract Authorization token from request
     */
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader(Common.TOKEN_HEADER_KEY);
        if (StringUtils.isBlank(authHeader)) {
            return null;
        } else {
            return authHeader.replace("Bearer ", "");
        }
    }
}