package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mmwwtt.demo.common.response.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mybatis-plus/user")
public class UserController {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @PostMapping("/saveOrUpdate")
    public ApiResponse<User> saveOrUpdate(@RequestBody User user) {
        userService.saveOrUpdate(user);
        return ApiResponse.success(user);
    }
    @PostMapping("/saveOrUpdateAll")
    public ApiResponse<List<User>> saveOrUpdateAll(@RequestBody List<User> userList) {
        userService.saveOrUpdateBatch(userList);
        return ApiResponse.success(userList);
    }

    @PostMapping("/query")
    public ApiResponse<List<User>> query(@RequestBody UserQuery query) {
        List<User> list = userService.queryList(query);
        return ApiResponse.success(list);
    }

    @PostMapping("/queryPage")
    public ApiResponse<Page<User>> queryPage(@RequestBody UserQuery query) {
        Page<User> page = userService.queryPage(query);
        return ApiResponse.success(page);
    }

}
