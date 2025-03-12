package com.mmwwtt.demo.common.response;

import java.io.Serial;
import java.io.Serializable;
import java.util.StringJoiner;

public class ApiResponse<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    private T data;

    public ApiResponse(Integer code,String message){
        this.code = code;
        this.message = message;
    }

    public ApiResponse(Integer code,String message,T data){
        this(code,message);
        this.data = data;
    }

    public static <T> ApiResponse<T> success(){
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(),ResponseCode.SUCCESS.getMessage());
    }

    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(ResponseCode.SUCCESS.getCode(),ResponseCode.SUCCESS.getMessage(),data);
    }

    public static <T> ApiResponse<T> fail(){
        return new ApiResponse<>(ResponseCode.FAILURE.getCode(),ResponseCode.FAILURE.getMessage());
    }

    public static <T> ApiResponse<T> fail(Integer code,String message){
        return new ApiResponse<>(code,message);
    }

    public static <T> ApiResponse<T> error(){
        return new ApiResponse<>(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.getMessage());
    }

    public void setCode(Integer code){
        this.code = code;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public void setData(T data){
        this.data = data;
    }
    public Integer getCode(){
        return this.code;
    }
    public String getMessage(){
        return this.message;
    }
    public T getData(){
        return this.data;
    }

    @Override
    public String toString() {
        return (new StringJoiner(", ", ApiResponse.class.getSimpleName() + "[", "]"))
                .add("code=" + this.code)
                .add("message='" + this.message + "'")
                .add("data=" + this.data)
                .toString();
    }
}
