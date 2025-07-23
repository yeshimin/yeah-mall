package com.yeshimin.yeahboot.auth.service;

import cn.hutool.core.util.StrUtil;
import com.yeshimin.yeahboot.auth.common.properties.AuthTokenProperties;
import com.yeshimin.yeahboot.auth.domain.dto.AuthDto;
import com.yeshimin.yeahboot.auth.domain.model.UserDetail;
import com.yeshimin.yeahboot.auth.domain.vo.AuthVo;
import com.yeshimin.yeahboot.auth.domain.vo.JwtPayloadVo;
import com.yeshimin.yeahboot.common.common.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 鉴权服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TokenService tokenService;

    private final List<UserDetailService> userDetailServices;
    private Map<String, UserDetailService> mapUserDetailService;

    private final AuthTokenProperties authTokenProperties;

    @PostConstruct
    public void init() {
        mapUserDetailService = userDetailServices.stream().collect(
                Collectors.toMap(UserDetailService::getSubject, userDetailService -> userDetailService));
    }

    /**
     * 鉴权（认证和授权）（token方式）
     */
    public AuthVo auth(AuthDto dto) {
        AuthVo authVo = new AuthVo();

        // 认证
        JwtPayloadVo decodedResult = tokenService.decodeToken(dto.getToken());
        if (decodedResult == null) {
            log.error("token decode fail");
            authVo.setAuthenticated(false);
            return authVo;
        } else if (!Objects.equals(dto.getToken(), tokenService.getCacheToken(
                decodedResult.getSub(), decodedResult.getAud(), decodedResult.getTerm(), decodedResult.getIatMs()))) {
            log.error("token cache validation fail");
            authVo.setAuthenticated(false);
            return authVo;
        } else {
            authVo.setAuthenticated(true);
            authVo.setUserId(Long.valueOf(decodedResult.getAud()));
            authVo.setSubject(decodedResult.getSub());
            authVo.setTerminal(decodedResult.getTerm());
        }

        // 接口越权检查
        if (!this.checkApiPrivilegeEscape(decodedResult.getSub())) {
            authVo.setAuthenticated(false);
            return authVo;
        }

        // 刷新token过期时间
        tokenService.refreshTokenExpire(decodedResult.getSub(), decodedResult.getAud(),
                decodedResult.getTerm(), decodedResult.getIatMs());

//        // 是否只进行认证
//        if (dto.getOnlyAuthenticate()) {
//            return authVo;
//        }

        String userId = decodedResult.getAud();

        // 授权
        UserDetail userDetail = this.getUserDetailService(decodedResult.getSub()).getUserDetail(userId);
//        UserDetail userDetail = userDetailService.getUserDetail(userId);

        authVo.setAuthenticated(true);
        authVo.setUsername(userDetail.getUsername());
        authVo.setRoles(userDetail.getRoles());
        authVo.setResources(userDetail.getResources());
        return authVo;
    }

    // ================================================================================

    private UserDetailService getUserDetailService(String subject) {
        UserDetailService userDetailService = mapUserDetailService.get(subject);
        if (userDetailService == null) {
            throw new BaseException("UserDetailService for subject: " + subject + " not found");
        }
        return userDetailService;
    }

    private boolean checkApiPrivilegeEscape(String subject) {
        AuthTokenProperties.Subject subjectObj = authTokenProperties.getMapSubject().get(subject);
        String apiPrefix = subjectObj.getApiPrefix();
        if (StrUtil.isBlank(apiPrefix)) {
            log.info("subject: {}, apiPrefix is not set, skip", subject);
            return true;
        }

        boolean isEqOp = true;
        if (apiPrefix.startsWith("!")) {
            isEqOp = false;
            apiPrefix = apiPrefix.substring(1);
        }

        String requestUri = this.getRequest().getRequestURI();

        boolean success = false;
        if (isEqOp) {
            success = requestUri.startsWith(apiPrefix);
        } else {
            success = !requestUri.startsWith(apiPrefix);
        }

        log.info("isEqOp: {}, apiPrefix: {}, requestUri: {}, success: {}", isEqOp, apiPrefix, requestUri, success);

        return success;
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Objects.requireNonNull(attrs).getRequest();
    }
}
