package com.mmwwtt.demo.ee.validation;

import com.mmwwtt.demo.common.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/service/demo/ee")
@Slf4j
public class ValidationDemoUIAPI {
    @PostMapping("/validation")
    public ApiResponse<Void> demoValidation(@RequestBody @Validated BaseInfoCreateDTO baseInfoCreateDTO) {
        log.info("{}",baseInfoCreateDTO);
        return ApiResponse.success();
    }

    @PostMapping("/validation1")
    public ApiResponse<Void> demoValidation1(@RequestBody BaseInfoCreateDTO baseInfoCreateDTO) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        validator.validate(baseInfoCreateDTO);

        Set<ConstraintViolation<BaseInfoCreateDTO>> violations = validator.validate(baseInfoCreateDTO);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }


        return ApiResponse.success();
    }
}
