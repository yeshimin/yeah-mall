package com.yeshimin.yeahboot.common.common.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Web上下文工具
 */
public class WebContextUtils {

    private static final ThreadLocal<String> TOKEN = new ThreadLocal<>();
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<String> SUBJECT = new ThreadLocal<>();
    // 设备终端，值可能为空
    private static final ThreadLocal<String> TERMINAL = new ThreadLocal<>();
    // 用户角色集合
    private static final ThreadLocal<Set<String>> ROLES = new ThreadLocal<>();
    // 用户资源集合
    private static final ThreadLocal<Set<String>> RESOURCES = new ThreadLocal<>();

    public static String getToken() {
        return TOKEN.get();
    }

    public static void setToken(String token) {
        TOKEN.set(token);
    }

    public static void removeToken() {
        TOKEN.remove();
    }

    // ================================================================================

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static void setUserId(Long userId) {
        USER_ID.set(userId);
    }

    public static void removeUserId() {
        USER_ID.remove();
    }

    // ================================================================================

    public static String getUsername() {
        return USERNAME.get();
    }

    public static void setUsername(String username) {
        USERNAME.set(username);
    }

    public static void removeUsername() {
        USERNAME.remove();
    }

    // ================================================================================

    public static String getSubject() {
        return SUBJECT.get();
    }

    public static void setSubject(String subject) {
        SUBJECT.set(subject);
    }

    public static void removeSubject() {
        SUBJECT.remove();
    }

    // ================================================================================
    // terminal值可能为空

    public static String getTerminal() {
        return TERMINAL.get();
    }

    public static void setTerminal(String terminal) {
        TERMINAL.set(terminal);
    }

    public static void removeTerminal() {
        TERMINAL.remove();
    }

    // ================================================================================
    // 用户角色和资源

    public static Set<String> getRoles() {
        Set<String> roles = ROLES.get();
        return roles == null ? new HashSet<>(0) : roles;
    }

    public static void setRoles(Set<String> roles) {
        ROLES.set(roles);
    }

    public static void removeRoles() {
        ROLES.remove();
    }

    public static Set<String> getResources() {
        Set<String> resources = RESOURCES.get();
        return resources == null ? new HashSet<>(0) : resources;
    }

    public static void setResources(Set<String> resources) {
        RESOURCES.set(resources);
    }

    public static void removeResources() {
        RESOURCES.remove();
    }

    // ================================================================================

    public static void clear() {
        removeToken();
        removeUserId();
        removeUsername();
        removeSubject();
        removeTerminal();
        removeRoles();
        removeResources();
    }
}