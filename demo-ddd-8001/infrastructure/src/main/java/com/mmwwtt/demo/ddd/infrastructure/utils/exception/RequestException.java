package com.mmwwtt.demo.ddd.infrastructure.utils.exception;

import com.mmwwtt.demo.ddd.infrastructure.utils.ResponseData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * validation的全局异常处理
 * @ControllerAdvice   全局异常处理
 * @ExceptionHandler({MethodArgumentNotValidException.class})  可以捕获的异常类型
 * @ResponseBody  错误返回都是json形式的数据返回
 */
@Slf4j
@ControllerAdvice
public class RequestException {


    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseBody
    public ResponseData<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errorMessage.append(fieldError.getDefaultMessage()).append("；");
        }
        errorMessage.deleteCharAt(errorMessage.length()-1).append("。");
        return new ResponseData<>().error(errorMessage.toString());
    }
}
