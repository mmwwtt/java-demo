package com.mmwwtt.demo.ee.validation;

import com.mmwwtt.demo.common.response.ApiResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/demo/ee")
public class ValidationDemo {
    @PostMapping("/validation")
    public ApiResponse<Void> demoValidation(@Validated BaseInfoCreateDTO baseInfoCreateDTO) {
        System.out.println(baseInfoCreateDTO);
        return ApiResponse.success();
    }

    public static void main(String[] args) {
        String str = "[].[]";
        Object object = str.split(".");
        String strt = str.substring(1,1);
        System.out.println(object);
    }
}
