package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmwwtt.demo.common.response.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mybatis-plus")
public class MybatisPlusController {

    @Resource
    private BaseInfoMybatisPlusDao dao;

    @PostMapping("/base-info/save")
    public ApiResponse<Void> save(@RequestBody BaseInfoMyBatisPlus baseInfoMyBatisPlus) {
        dao.insert(baseInfoMyBatisPlus);
        return ApiResponse.success();
    }

    @PostMapping("/base-info/query")
    public ApiResponse<List<BaseInfoMyBatisPlus>> query(@RequestBody BaseInfoMybatisPlusQuery studentQuery) {
        List<BaseInfoMyBatisPlus> list = dao.query(studentQuery);
        return ApiResponse.success(list);
    }

    @PostMapping("/base-info/queryByPage")
    public ApiResponse<Page<BaseInfoMyBatisPlus>> queryByPage(@RequestBody BaseInfoMybatisPlusQuery studentQuery) {
        Page<BaseInfoMyBatisPlus> page = dao.queryByPage(studentQuery);
        return ApiResponse.success(page);
    }

}
