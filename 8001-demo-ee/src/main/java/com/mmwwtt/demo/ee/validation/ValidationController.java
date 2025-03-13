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
@RequestMapping("/validation")
@Slf4j
public class ValidationController {
    @PostMapping("/demo1")
    public ApiResponse<Void> demo1(@RequestBody @Validated BaseInfoValidation baseInfoValidation) {
        log.info("{}", baseInfoValidation);
        return ApiResponse.success();
    }

    /**
     * 代码中对进行校验
     * @param baseInfoValidation
     * @return
     */
    @PostMapping("/demo2")
    public ApiResponse<Void> demo2(@RequestBody BaseInfoValidation baseInfoValidation) {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        validator.validate(baseInfoValidation);

        Set<ConstraintViolation<BaseInfoValidation>> violations = validator.validate(baseInfoValidation);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(new HashSet<>(violations));
        }
        return ApiResponse.success();
    }
}
