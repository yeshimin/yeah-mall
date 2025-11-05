package com.yeshimin.yeahboot.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * RateLimit 拦截器
 * TODO 考虑并发，reqTime一样的情况，加入uuid或其他可以标识请求唯一性的东西
 * TODO 还是并发，组合操作非原子（原子操作之间未加锁），可能导致瞬间超过阈值
 * TODO 支持spel
 * TODO 支持global模式（分布式缓存模式）
 * TODO 清理过期一段时间的记录
 */
@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, ConcurrentLinkedDeque<Long>> plainCounters = new ConcurrentHashMap<>();
    private final Map<String, Map<String, ConcurrentLinkedDeque<Long>>> groupCounters = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentLinkedDeque<Long>> effectPlainCounters = new ConcurrentHashMap<>();
    private final Map<String, Map<String, ConcurrentLinkedDeque<Long>>> effectGroupCounters = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            log.info("Not a method handler, return");
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        RateLimit rateLimit = method.getAnnotation(RateLimit.class);

        // 没有注解或禁用限流
        if (rateLimit == null || !rateLimit.enabled()) {
            log.info("No rate limit annotation or disabled, return");
            return true;
        }

        // --------------------------------------------------------------------------------

        // 获取限流配置
        RateLimitConf rlConf = this.getRateLimitConf(rateLimit, method, request);

        // 记录请求，不管该请求是否被限流
        this.plainRecord(plainCounters, rlConf);

        // 记录有效请求，即未被拦截的请求
        boolean plainAllowed = this.effectPlainRecord(effectPlainCounters, rlConf);
        log.info("plainAllowed: {}", plainAllowed);
        if (!plainAllowed) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
            return false;
        }

        // 记录请求分组，不管该请求是否被限流
        this.groupRecord(groupCounters, rlConf);

        // 记录有效请求分组，即未被拦截的请求
        boolean groupAllowed = this.effectGroupRecord(effectGroupCounters, rlConf);
        log.info("groupAllowed: {}", groupAllowed);
        if (!groupAllowed) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
            return false;
        }

        return true;
    }

    private boolean effectGroupRecord(Map<String, Map<String, ConcurrentLinkedDeque<Long>>> effectGroupCounters, RateLimitConf rlConf) {
        Map<String, ConcurrentLinkedDeque<Long>> map = effectGroupCounters.computeIfAbsent(rlConf.getResName(), k -> new ConcurrentHashMap<>());
        log.info("effectGroupRecord...map: {}", map);

        // 过滤出分组最后一次请求在时间窗口内的记录
        Set<String> validGroupKeys = ConcurrentHashMap.newKeySet();
        for (Map.Entry<String, ConcurrentLinkedDeque<Long>> entry : map.entrySet()) {
            ConcurrentLinkedDeque<Long> list = entry.getValue();
            log.info("effectGroupRecord...list.size: {}", list.size());
            if (list.isEmpty()) {
                continue;
            }
            long lastTime = list.getLast();
            log.info("effectGroupRecord...lastTime: {}, windowFrom: {}", lastTime, rlConf.getWindowFrom());
            if (lastTime >= rlConf.getWindowFrom()) {
                validGroupKeys.add(entry.getKey());
            }
        }
        log.info("effectGroupRecord...validGroupKeys: {}", validGroupKeys);
        boolean groupAllowed = true;
        if (rlConf.getLimitGroup() > -1) {
            if (validGroupKeys.contains(rlConf.getGroupName())) {
                groupAllowed = validGroupKeys.size() <= rlConf.getLimitGroup();
            } else {
                groupAllowed = validGroupKeys.size() + 1 <= rlConf.getLimitGroup();
            }
        }

        // 检查limitGroupCount
        boolean groupCountAllowed = true;
        ConcurrentLinkedDeque<Long> list = map.computeIfAbsent(rlConf.getGroupName(), k -> new ConcurrentLinkedDeque<>());
        log.info("effectGroupRecord...list.size: {}", list.size());
        // 倒序遍历，统计窗口内记录数
        int count = 0;
        Iterator<Long> iterator = list.descendingIterator();
        while (iterator.hasNext()) {
            long time = iterator.next();
            if (time >= rlConf.getWindowFrom()) {
                count++;
            } else {
                break;
            }
        }

        log.info("effectGroupRecord...count: {}, windowFrom: {}", count, rlConf.getWindowFrom());
        if (rlConf.getLimitGroupCount() > -1 && count + 1 > rlConf.getLimitGroupCount()) {
            groupCountAllowed = false;
        }

        if (groupAllowed && groupCountAllowed) {
            list.add(rlConf.getReqTime());
            return true;
        } else {
            return false;
        }
    }

    private void plainRecord(Map<String, ConcurrentLinkedDeque<Long>> plainCounters, RateLimitConf rlConf) {
        ConcurrentLinkedDeque<Long> list = plainCounters.computeIfAbsent(rlConf.getResName(), k -> new ConcurrentLinkedDeque<>());
        list.add(rlConf.getReqTime());
        log.info("plainRecord...list.size: {}", list.size());
    }

    private String getGroupName(RateLimitConf rlConf, HttpServletRequest request) {
        switch (rlConf.getGroupType()) {
            case DEFAULT:
                return String.valueOf(rlConf.getWindowFrom());
            case IP:
                return request.getRemoteAddr();
            case USER:
                // 假设用户ID在请求头 X-User-Id 中
//                String userId = request.getHeader("X-User-Id");
//                return baseKey + ":" + (userId != null ? userId : "anonymous");
                return ""; // TODO
            case CUSTOM:
                return rlConf.getCustomGroup();
            default:
                return String.valueOf(rlConf.getWindowFrom());
        }
    }

    /**
     * 记录有效请求
     */
    private boolean effectPlainRecord(Map<String, ConcurrentLinkedDeque<Long>> effectGroupCounters, RateLimitConf rlConf) {
        ConcurrentLinkedDeque<Long> list = effectGroupCounters.computeIfAbsent(rlConf.getResName(), k -> new ConcurrentLinkedDeque<>());

        // 倒序遍历，统计窗口内记录数
        int count = 0;
        Iterator<Long> iterator = list.descendingIterator();
        while (iterator.hasNext()) {
            long time = iterator.next();
            if (time >= rlConf.getWindowFrom()) {
                count++;
            } else {
                break;
            }
        }
        if (rlConf.getLimitCount() > -1 && count + 1 > rlConf.getLimitCount()) {
            return false;
        }
        list.add(rlConf.getReqTime());
        return true;
    }

    private void groupRecord(Map<String, Map<String, ConcurrentLinkedDeque<Long>>> groupCounters, RateLimitConf rlConf) {
        Map<String, ConcurrentLinkedDeque<Long>> map = groupCounters.computeIfAbsent(rlConf.getResName(), k -> new ConcurrentHashMap<>());
        ConcurrentLinkedDeque<Long> list = map.computeIfAbsent(rlConf.getGroupName(), k -> new ConcurrentLinkedDeque<>());
        list.add(rlConf.getReqTime());
        log.info("groupRecord...list.size: {}", list.size());
    }

    /**
     * 获取RateLimitConf配置
     */
    private RateLimitConf getRateLimitConf(RateLimit rl, Method m, HttpServletRequest req) {
        RateLimitConf conf = new RateLimitConf();
        conf.setEnabled(rl.enabled());
        conf.setName(rl.name());
        conf.setGroupType(rl.groupType());
        conf.setCustomGroup(rl.customGroup());
        conf.setLimitCount(rl.limitCount());
        conf.setLimitGroup(rl.limitGroup());
        conf.setLimitGroupCount(rl.limitGroupCount());
        conf.setTimeWindow(rl.timeWindow());
        conf.setDynamicTimeWindow(rl.dynamicTimeWindow());
        conf.setGlobal(rl.global());

        // 请求时间
        conf.setReqTime(System.currentTimeMillis());

        // 修整参数
        if (conf.getTimeWindow() <= 1) {
            conf.setTimeWindow(1);
        }

        // 计算窗口时间范围
        this.calcWindowTimeRange(conf);

        // 资源名称
        conf.setResName(this.getResName(conf, m));
        // 分组名称
        conf.setGroupName(this.getGroupName(conf, req));

        log.info("RateLimitConf: {}", conf);

        return conf;
    }

    /**
     * 计算时间窗口起始时间
     * 根据是否动态时间窗口选择计算方式
     */
    private void calcWindowTimeRange(RateLimitConf rlConf) {
        if (rlConf.isDynamicTimeWindow()) {
            rlConf.setWindowFrom(rlConf.getReqTime() - rlConf.getTimeWindow() * 1000);
            rlConf.setWindowTo(rlConf.getReqTime());
        } else {
            rlConf.setWindowFrom(rlConf.getReqTime() / 1000 / rlConf.getTimeWindow() * rlConf.getTimeWindow() * 1000);
            rlConf.setWindowTo(rlConf.getWindowFrom() + rlConf.getTimeWindow() * 1000);
        }
    }

    /**
     * 获取目标资源的名称（一般为接口方法的全限定名称或者自定义名称）
     */
    private String getResName(RateLimitConf rlConf, Method m) {
        return rlConf.getName().isEmpty() ? m.getDeclaringClass().getName() + "." + m.getName() : rlConf.getName();
    }
}
