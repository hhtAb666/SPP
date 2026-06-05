/**
 * 统一 API 响应结果封装类
 */
package com.campus.sport.common;

// 统一返回结果类

import lombok.Data;


@Data
// 统一API响应结果封装
public class Result<T> {

    private Integer code; 
    private String msg;   
    private T data;       

    
    public Result() {
    }

    
    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "操作成功", data);
    }
    
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }
    
    public static <T> Result<T> success() {
        return new Result<>(200, "操作成功", null);
    }

    
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }
    
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }
}