package com.mmwwtt.demo.ee.mybatisPlus;

import com.mmwwtt.demo.common.response.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/demo/ee/mybatisPlus")
public class MybatisPlusDemoUIAPI {

    @Resource
    private MybatisPlusStudentMapper studentMapper;

    @PostMapping("/student/save")
    public ApiResponse<Void> demoValidation(MybatisPlusStudent mybatisPlusStudent) {
        studentMapper.insert(mybatisPlusStudent);
        return ApiResponse.success();
    }
}
