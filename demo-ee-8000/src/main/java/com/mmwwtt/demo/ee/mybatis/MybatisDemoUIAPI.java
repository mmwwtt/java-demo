package com.mmwwtt.demo.ee.mybatis;

import com.mmwwtt.demo.common.response.ApiResponse;
import com.mmwwtt.demo.ee.validation.BaseInfoCreateDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/demo/ee/mybatis")
public class MybatisDemoUIAPI {

    @PostMapping("/demo1")
    public ApiResponse<Void> demoValidation(@Validated BaseInfoCreateDTO baseInfoCreateDTO) {
        System.out.println(baseInfoCreateDTO);
        return ApiResponse.success();
    }
}
