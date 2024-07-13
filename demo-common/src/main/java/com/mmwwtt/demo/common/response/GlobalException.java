package com.mmwwtt.demo.common.response;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalException {

    private static final Logger LOGGER = LogManager.getLogger(GlobalException.class);
    @ExceptionHandler({Exception.class})
    public ApiResponse<Void> exception(Exception e) {
        LOGGER.info("全局异常信息 ex={}", e.getMessage(), e);
        return ApiResponse.error();
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ApiResponse<Void> exception(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<String> errorMessageList = bindingResult.getAllErrors()
                .stream().map(ObjectError::getDefaultMessage)
                .toList();
        return ApiResponse.fail(ResponseCode.ERROR.getCode(), errorMessageList.toString());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ApiResponse<Void> exception(ConstraintViolationException e) {
        return ApiResponse.fail(ResponseCode.ERROR.getCode(), e.getMessage());
    }



    @ExceptionHandler(value = CustomException.class)
    public ApiResponse<Void> CustomExceptionHandler(CustomException e) {
        return ApiResponse.fail(e.getCode(),e.getMessage());
    }
}

