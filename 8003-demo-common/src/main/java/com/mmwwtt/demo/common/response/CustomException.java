package com.mmwwtt.demo.common.response;

public class CustomException extends RuntimeException{
    protected Integer code;
    protected String message;


    public CustomException(Integer code,String message,Throwable e) {
        super(message,e);
        this.code = code;
        this.message = message;
    }

    public CustomException(Integer code,String message){
        this(code,message,null);
    }

    public void setCode(Integer code){
        this.code = code;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public Integer getCode(){
        return this.code;
    }

}
