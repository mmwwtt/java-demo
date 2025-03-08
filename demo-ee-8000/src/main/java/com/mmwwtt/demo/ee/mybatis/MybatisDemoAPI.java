package com.mmwwtt.demo.ee.mybatis;

import com.mmwwtt.demo.common.response.ApiResponse;
import com.mmwwtt.demo.ee.validation.BaseInfoCreateDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/demo/ee/mybatis")
@Slf4j
public class MybatisDemoAPI {

    @PostMapping("/demo1")
    public ApiResponse<Void> demoValidation(@Validated BaseInfoCreateDTO baseInfoCreateDTO) {
        log.info("{}",baseInfoCreateDTO);
        return ApiResponse.success();
    }
}
