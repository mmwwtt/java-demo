package com.mmwwtt.demo.common.response;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler({Exception.class})
    public ApiResponse<Void> exception(Exception e) {
//        log.error("全局异常信息 ex={}", e.getMessage(), e);
        return ApiResponse.error();
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ApiResponse<Void> exception(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        ObjectError objectError = bindingResult.getAllErrors().stream().findFirst().get();
        String messages = objectError.getDefaultMessage();
        return ApiResponse.fail(ResponseCode.ERROR.getCode(), messages);
    }

    @ExceptionHandler(value = CustomException.class)
    public ApiResponse<Void> CustomExceptionHandler(CustomException e) {
        return ApiResponse.fail(e.getCode(),e.getMessage());
    }
}

