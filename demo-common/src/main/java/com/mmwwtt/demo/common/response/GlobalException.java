package com.mmwwtt.demo.common.response;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalException {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(GlobalException.class);
    @ExceptionHandler({Exception.class})
    public ApiResponse<Void> exception(Exception e) {
        LOGGER.error("全局异常信息 ex={}", e.getMessage(), e);
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

