package com.mmwwtt.demo.ee.mybatisPlus;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmwwtt.demo.common.response.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/service/demo/ee/mybatisPlus")
public class MybatisPlusDemoUIAPI {

    @Resource
    private MybatisPlusStudentMapper studentMapper;

    @PostMapping("/student/save")
    public ApiResponse<Void> demoValidation(@RequestBody MybatisPlusStudent mybatisPlusStudent) {
        studentMapper.insert(mybatisPlusStudent);
        return ApiResponse.success();
    }

    @PostMapping("/student/queryByPage")
    public ApiResponse<Page<MybatisPlusStudent>> queryByPage(@RequestBody MybatisPlusStudentQuery mybatisPlusStudentQuery) {
        Page<MybatisPlusStudent> page = studentMapper.selectPageByQuery(mybatisPlusStudentQuery);
        return ApiResponse.success(page);
    }

    @PostMapping("/student/query")
    public ApiResponse<List<MybatisPlusStudent>> query(@RequestBody MybatisPlusStudentQuery mybatisPlusStudentQuery) {
        List<MybatisPlusStudent> list = studentMapper.selectByQuery(mybatisPlusStudentQuery);
        return ApiResponse.success(list);
    }

}
