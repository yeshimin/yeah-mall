package com.yeshimin.yeahboot.auth.common.config.security;

import com.yeshimin.yeahboot.auth.service.AuthService;
import com.yeshimin.yeahboot.common.common.log.MdcLogFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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

    // resolve: 引入actuator后，其中的'controllerEndpointHandlerMapping'会和'requestMappingHandlerMapping'冲突
    public WebSecurityConfig(AuthService authService,
                             @Qualifier("requestMappingHandlerMapping") RequestMappingHandlerMapping handlerMapping) {
        this.authService = authService;
        this.handlerMapping = handlerMapping;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.cors().disable();
        // 不需要session
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        Map<String, PublicAccess> publicAccessUrls = this.getPublicAccessUrls();
        // for springdoc
        publicAccessUrls.putAll(this.getUrlsForSpringdoc());
        log.info("Public access URLs: {}", publicAccessUrls);

        http.addFilterAfter(new JwtTokenAuthenticationFilter(authenticationManagerBean(), publicAccessUrls), LogoutFilter.class);
        // mdc filter设置到认证filter之前，使相关日志尽早附带mdc信息
        http.addFilterBefore(new MdcLogFilter(), JwtTokenAuthenticationFilter.class);

        http.authorizeRequests()
//                .antMatchers("/**/auth/login", "/admin/auth/captcha", "/public/storage/download")
                .antMatchers("/actuator/health", "/actuator/info")
                .permitAll()
                // --------------------------------------------------------------------------------
                .antMatchers(publicAccessUrls.keySet().toArray(new String[0]))
                .permitAll()
                .anyRequest().authenticated()
                .and()
                // 自定义认证失败处理
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler());
    }

    /**
     * 如果重写了authenticationManagerBean()，需要同时重写该方法
     *
     * @see WebSecurityConfigurerAdapter#authenticationManagerBean()
     */
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

    // ================================================================================

    private Map<String, PublicAccess> getPublicAccessUrls() {
//        Set<String> urls = new HashSet<>();
        Map<String, PublicAccess> urls = new HashMap<>();

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();

            // 判断是否打了 @PublicAccess 且 enabled = true
            PublicAccess access = handlerMethod.getMethodAnnotation(PublicAccess.class);
            if (access != null && access.enabled()) {
                RequestMappingInfo mappingInfo = entry.getKey();

                // 获取该方法映射的 URL 路径
                Set<String> patterns = new HashSet<>();
                if (mappingInfo.getPathPatternsCondition() != null) {
                    patterns = mappingInfo.getPathPatternsCondition().getPatternValues();
                } else if (mappingInfo.getPatternsCondition() != null) {
                    patterns = mappingInfo.getPatternsCondition().getPatterns();
                } else {
                    log.warn("No patterns found for method: {}", handlerMethod.getMethod().getName());
                }
//                urls.addAll(patterns);
                for (String pattern : patterns) {
                    urls.put(pattern, access);
                }
            }
        }

        return urls;
    }

    /**
     * 获取静态资源路径 for springdoc
     */
    private Map<String, PublicAccess> getUrlsForSpringdoc() {
        Set<String> paths = new HashSet<>();
        paths.add("/v3/api-docs/**");
        paths.add("/**/*.html");
        paths.add("/**/*.js");
        paths.add("/**/*.css");

        Map<String, PublicAccess> urls0 = new HashMap<>();
        for (String path : paths) {
            urls0.put(path, null);
        }
        return urls0;
    }
}