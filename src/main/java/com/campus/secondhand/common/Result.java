package com.campus.secondhand.common;

import lombok.Data;

/**
 * 统一返回结果
 * @param <T>
 */
@Data
public class Result <T>{
    /**
     * 状态码
     */
    private Integer code;

    // 提示消息
    private String message;

    // 返回数据
    private T data;

    // 空参构造
    public Result(){

    }
    // 带全部参数构造
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data){
        return new Result<>(200,"success",data);
    }

    public static <T> Result<T> success(){
        return new Result<>(200,"success",null);
    }

    public static <T> Result<T> error(String message){
        return new Result<>(500,message,null);
    }

}
