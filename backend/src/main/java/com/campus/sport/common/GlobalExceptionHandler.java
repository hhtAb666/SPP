/**
 * 全局异常处理器
 */
package com.campus.sport.common;

// 全局异常处理器

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotRoleException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;


@Slf4j
@RestControllerAdvice
// 全局异常处理器
public class GlobalExceptionHandler {

    
    @ExceptionHandler(NotLoginException.class)
    public Result<?> handlerNotLoginException(NotLoginException nle) {
        return Result.error(401, "请先登录");
    }

    
    @ExceptionHandler(NotRoleException.class)
    public Result<?> handlerNotRoleException(NotRoleException nre) {
        return Result.error(403, "无权操作");
    }

    
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<?> handleMissingParams(MissingServletRequestParameterException e) {
        String paramName = e.getParameterName();
        return Result.error(400, "参数缺失: " + paramName);
    }

    
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        String paramName = e.getName();
        return Result.error(400, "参数类型错误: " + paramName);
    }

    
    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKey(DuplicateKeyException e) {
        return Result.error("数据已存在，请勿重复操作");
    }
    
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Result<?> handleDataIntegrity(DataIntegrityViolationException e) {
        log.error("数据库操作异常", e);
        return Result.error("数据操作失败，请检查数据完整性");
    }

    
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("业务异常", e);
        
        return Result.error(e.getMessage());
    }

    
    @ExceptionHandler(Exception.class)
    public Result<?> handlerException(Exception e) {
        log.error("系统异常", e);
        return Result.error("系统繁忙，请稍后重试");
    }
}