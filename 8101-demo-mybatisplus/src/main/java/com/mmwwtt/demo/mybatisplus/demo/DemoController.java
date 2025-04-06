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
public class DemoController {

    @Resource
    private BaseInfoDao dao;

    @PostMapping("/base-info/save")
    public ApiResponse<Void> save(@RequestBody BaseInfo baseInfo) {
        dao.insert(baseInfo);
        return ApiResponse.success();
    }

    @PostMapping("/base-info/query")
    public ApiResponse<List<BaseInfo>> query(@RequestBody BaseInfoQuery baseInfoQuery) {
        List<BaseInfo> list = dao.query(baseInfoQuery);
        return ApiResponse.success(list);
    }

    @PostMapping("/base-info/queryByPage")
    public ApiResponse<Page<BaseInfo>> queryByPage(@RequestBody BaseInfoQuery baseInfoQuery) {
        Page<BaseInfo> page = dao.queryByPage(baseInfoQuery);
        return ApiResponse.success(page);
    }

}
