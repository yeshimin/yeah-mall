package com.yeshimin.yeahboot.ratelimit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * RateLimit 拦截器
 * TODO 考虑并发，reqTime一样的情况，加入uuid或其他可以标识请求唯一性的东西
 * TODO 支持spel
 * TODO 支持global模式（分布式缓存模式）
 * TODO log.info -> log.debug
 * TODO 后台清除过期的
 */
@Slf4j
@Component
public class RateLimitInterceptor2 implements HandlerInterceptor {

    private final Map<String, ConcurrentLinkedDeque<Long>> plainRecords = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> plainCounter = new ConcurrentHashMap<>();

    private final Map<String, Map<String, Long>> outGroupRecords = new ConcurrentHashMap<>();

    private final Map<String, Map<String, ConcurrentLinkedDeque<Long>>> groupRecords = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> groupCounter = new ConcurrentHashMap<>();

    private final Map<String, ConcurrentLinkedQueue<PlainRecord>> mapPlainRecords0 = new ConcurrentHashMap<>();
    private final Map<String, ConcurrentLinkedQueue<InnerGroupRecord>> mapInnerGroupRecords0 = new ConcurrentHashMap<>();
    private final Map<String, OuterGroupRecord> mapOuterGroupRecords0 = new ConcurrentHashMap<>();

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

        // --------------------------------------------------------------------------------

        // 记录请求

        ConcurrentLinkedQueue<PlainRecord> plainRecords0 = mapPlainRecords0.computeIfAbsent(rlConf.getResName(), k -> new ConcurrentLinkedQueue<>());
        PlainRecord plainRecord = new PlainRecord(rlConf.getReqTime(), false);
        plainRecords0.add(plainRecord);

        String resGroupName = rlConf.getResName() + ":" + rlConf.getGroupName();
        ConcurrentLinkedQueue<InnerGroupRecord> innerGroupRecords0 = mapInnerGroupRecords0.computeIfAbsent(resGroupName, k -> new ConcurrentLinkedQueue<>());
        InnerGroupRecord innerGroupRecord = new InnerGroupRecord(rlConf.getReqTime(), false);
        innerGroupRecords0.add(innerGroupRecord);

        OuterGroupRecord outerGroupRecord = mapOuterGroupRecords0.computeIfAbsent(resGroupName, k -> new OuterGroupRecord(0, rlConf.getReqTime()));
        if (rlConf.getReqTime() > outerGroupRecord.getLastReqTime()) {
            outerGroupRecord.setLastReqTime(rlConf.getReqTime());
        }

        // 统计有效请求，判断和拦截

        boolean allowed = true;

        if (rlConf.getLimitCount() > -1) {
            long plainCount = plainRecords0.stream().filter(record -> record.getTime() >= rlConf.getWindowFrom() && record.isAllowed()).count();
        }








        // 记录有效请求，即未被拦截的请求
        boolean plainAllowed = this.plainHandle(rlConf);
        log.info("plainAllowed: {}", plainAllowed);
        if (!plainAllowed) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
            return false;
        }

        // 记录有效请求分组，即未被拦截的请求
        boolean groupAllowed = this.groupHandle(rlConf);
        log.info("groupAllowed: {}", groupAllowed);
        if (!groupAllowed) {
            response.setStatus(429); // Too Many Requests
            response.getWriter().write("Rate limit exceeded");
            return false;
        }

        return true;
    }

    /**
     * 记录有效请求
     */
    private boolean plainHandle(RateLimitConf rlConf) {
        ConcurrentLinkedDeque<Long> records = plainRecords.computeIfAbsent(rlConf.getResName(), k -> new ConcurrentLinkedDeque<>());
        AtomicInteger counter = plainCounter.computeIfAbsent(rlConf.getResName(), k -> new AtomicInteger());

        // 过滤出时间窗口内的记录
//        ConcurrentLinkedDeque<Long> validRecords = records.stream().filter(record -> record >= rlConf.getWindowFrom()).collect(Collectors.toList())

        // 清理窗口外过期记录
        while (true) {
            Long head = records.peekFirst();
            if (head != null && head < rlConf.getWindowFrom()) {
                records.pollFirst();
                counter.decrementAndGet();
            } else {
                break;
            }
        }

        while (true) {
            int current = counter.get();
            if (rlConf.getLimitCount() > -1 && current >= rlConf.getLimitCount()) {
                return false;
            }
            if (counter.compareAndSet(current, current + 1)) {
                records.addLast(rlConf.getReqTime());
                return true;
            }
        }
    }

    private boolean groupHandle(RateLimitConf rlConf) {
        // 检查limitGroup
        Map<String, Long> outRecords = outGroupRecords.computeIfAbsent(rlConf.getResName(), k -> new ConcurrentHashMap<>());

        // 清除过期记录
        outRecords.entrySet().removeIf(entry -> entry.getValue() < rlConf.getWindowFrom());

        boolean groupAllowed = true;
        if (rlConf.getLimitGroup() > -1) {
            if (outRecords.containsKey(rlConf.getGroupName())) {
                groupAllowed = outRecords.size() <= rlConf.getLimitGroup();
            } else {
                groupAllowed = outRecords.size() + 1 <= rlConf.getLimitGroup();
            }
        }

        // 检查limitGroupCount
        Map<String, ConcurrentLinkedDeque<Long>> records = groupRecords.computeIfAbsent(rlConf.getResName(), k -> new ConcurrentHashMap<>());

        boolean groupCountAllowed = true;
        ConcurrentLinkedDeque<Long> list = records.computeIfAbsent(rlConf.getGroupName(), k -> new ConcurrentLinkedDeque<>());
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
            outRecords.put(rlConf.getGroupName(), rlConf.getReqTime());
            list.add(rlConf.getReqTime());
            return true;
        } else {
            return false;
        }
    }

    private String getGroupName(RateLimitConf rlConf, HttpServletRequest request) {
        switch (rlConf.getGroupType()) {
            case IP:
                return request.getRemoteAddr();
            case CUSTOM:
                return rlConf.getCustomGroup();
            case NONE:
            default:
                return String.valueOf(rlConf.getWindowFrom());
        }
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

    @Data
    @AllArgsConstructor
    private static class PlainRecord {
        private long time;
        private volatile boolean allowed;
    }

    @Data
    @AllArgsConstructor
    private static class InnerGroupRecord {
        private long time;
        private volatile boolean allowed;
    }

    @Data
    @AllArgsConstructor
    private static class OuterGroupRecord {
        private volatile long lastAllowedTime;
        private volatile long lastReqTime;
    }
}
