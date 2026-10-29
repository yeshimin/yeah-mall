package com.yeshimin.yeahboot.auth.common.config.security;

import com.yeshimin.yeahboot.api.encryption.ApiEncryptionController;
import com.yeshimin.yeahboot.api.encryption.ApiEncryptionQueryFilter;
import com.yeshimin.yeahboot.auth.service.AuthService;
import com.yeshimin.yeahboot.common.common.log.MdcLogFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthService authService;
    private final RequestMappingHandlerMapping handlerMapping;
    private final ApiEncryptionQueryFilter apiEncryptionQueryFilter;

    @Value("${springdoc.api-docs.enabled:true}")
    private boolean springdocApiDocsEnabled;

    @Value("${auth.token.support-query-token:true}")
    private boolean supportQueryToken;

    // 显式限定 requestMappingHandlerMapping，避免 actuator 的映射 Bean 冲突。
    public WebSecurityConfig(AuthService authService,
                             @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping,
                             ApiEncryptionQueryFilter apiEncryptionQueryFilter) {
        this.authService = authService;
        this.handlerMapping = handlerMapping;
        this.apiEncryptionQueryFilter = apiEncryptionQueryFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        Map<String, PublicAccess> publicAccessUrls = this.getPublicAccessUrls();
        publicAccessUrls.put(ApiEncryptionController.PUBLIC_KEY_PATH, null);
        if (springdocApiDocsEnabled) {
            publicAccessUrls.putAll(this.getUrlsForSpringdoc());
        }
        log.info("Public access URLs: {}", publicAccessUrls);

        http.addFilterAfter(
                new JwtTokenAuthenticationFilter(authenticationManagerBean(), publicAccessUrls, supportQueryToken),
                LogoutFilter.class);
        // Spring Security 5 的 authorizeRequests 使用 FilterSecurityInterceptor 完成路径授权。
        http.addFilterAfter(apiEncryptionQueryFilter, FilterSecurityInterceptor.class);
        http.addFilterBefore(new MdcLogFilter(), JwtTokenAuthenticationFilter.class);

        http.authorizeRequests()
                .antMatchers("/actuator/health", "/actuator/info")
                .permitAll()
                .antMatchers(publicAccessUrls.keySet().toArray(new String[0]))
                .permitAll()
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new JwtTokenAuthenticationProvider(authService);
    }

    private Map<String, PublicAccess> getPublicAccessUrls() {
        Map<String, PublicAccess> urls = new HashMap<>();
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            PublicAccess access = handlerMethod.getMethodAnnotation(PublicAccess.class);
            if (access == null || !access.enabled()) {
                continue;
            }

            RequestMappingInfo mappingInfo = entry.getKey();
            Set<String> patterns = new HashSet<>();
            if (mappingInfo.getPathPatternsCondition() != null) {
                patterns = mappingInfo.getPathPatternsCondition().getPatternValues();
            } else if (mappingInfo.getPatternsCondition() != null) {
                patterns = mappingInfo.getPatternsCondition().getPatterns();
            } else {
                log.warn("No patterns found for method: {}", handlerMethod.getMethod().getName());
            }
            for (String pattern : patterns) {
                urls.put(pattern, access);
            }
        }

        return urls;
    }

    private Map<String, PublicAccess> getUrlsForSpringdoc() {
        Set<String> paths = new HashSet<>();
        paths.add("/v3/api-docs/**");
        paths.add("/**/*.html");
        paths.add("/**/*.js");
        paths.add("/**/*.css");

        Map<String, PublicAccess> urls = new HashMap<>();
        for (String path : paths) {
            urls.put(path, null);
        }
        return urls;
    }
}
