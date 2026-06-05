/**
 * 操作日志 AOP 切面处理类
 */
package com.campus.sport.aspect;

// 日志切面类

import cn.dev33.satoken.stp.StpUtil;
import com.campus.sport.annotation.Log;
import com.campus.sport.entity.SysLog;
import com.campus.sport.service.SysLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
// 日志切面处理类
public class LogAspect {

    private final SysLogService sysLogService;
    private final ObjectMapper objectMapper;

    @Pointcut("@annotation(com.campus.sport.annotation.Log)")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = point.proceed();
        long time = System.currentTimeMillis() - beginTime;

        saveSysLog(point, time);

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLog sysLog = new SysLog();
        Log logAnnotation = method.getAnnotation(Log.class);
        if (logAnnotation != null) {
            sysLog.setOperation(logAnnotation.value());
        }

        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");

        Object[] args = joinPoint.getArgs();
        try {
            String params = objectMapper.writeValueAsString(args);
            
            if (params.length() > 2000) {
                params = params.substring(0, 2000) + "...";
            }
            sysLog.setParams(params);
        } catch (Exception e) {
            sysLog.setParams("params serialization failed");
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        sysLog.setIp(request.getRemoteAddr());

        try {
            if (StpUtil.isLogin()) {
                
                
                sysLog.setUsername(StpUtil.getLoginIdAsString());
            } else {
                sysLog.setUsername("Unknown/Guest");
            }
        } catch (Exception e) {
            sysLog.setUsername("System");
        }

        sysLog.setTime(time);
        sysLog.setCreateTime(LocalDateTime.now());

        sysLogService.save(sysLog);
    }
}