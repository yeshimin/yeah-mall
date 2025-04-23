package com.yeshimin.yeahboot.upms.common.log;

import cn.hutool.json.JSONUtil;
import com.yeshimin.yeahboot.upms.domain.entity.SysLogEntity;
import com.yeshimin.yeahboot.upms.repository.SysLogRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 系统日志切面
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SysLogAspect {

    private final SysLogRepo sysLogRepo;

    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint joinPoint, SysLog sysLog) throws Throwable {
        long start = System.currentTimeMillis();
        SysLogEntity logEntity = new SysLogEntity();

        // 设置注解信息
        logEntity.setEvent(sysLog.value());
        logEntity.setTriggerType(sysLog.triggerType());
        logEntity.setCategory(sysLog.category());

        // 方法名、类名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        logEntity.setComment(method.getDeclaringClass().getName() + "#" + method.getName());

        // 收集输入参数
        try {
            Object[] args = joinPoint.getArgs();
            String inputJson = JSONUtil.toJsonStr(args);
            logEntity.setInput(inputJson);
        } catch (Exception e) {
            log.warn("参数序列化异常", e);
        }

        Object result = null;
        boolean success = true;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            success = false;
            logEntity.setSuccess("0");
            logEntity.setExtra("{\"exception\": \"" + e.getMessage() + "\"}");
            throw e;
        } finally {
            long end = System.currentTimeMillis();
            logEntity.setTime((int) (end - start));
            logEntity.setSuccess(success ? "1" : "0");

            // 收集输出结果
            try {
                String outputJson = JSONUtil.toJsonStr(result);
                logEntity.setOutput(outputJson);
            } catch (Exception e) {
                log.warn("结果序列化异常", e);
            }

            // 发布或保存日志
            sysLogRepo.save(logEntity);
        }
    }
}
