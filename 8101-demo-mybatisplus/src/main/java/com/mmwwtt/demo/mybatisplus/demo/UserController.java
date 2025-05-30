package com.mmwwtt.demo.mybatisplus.demo;

import com.mmwwtt.demo.common.response.ApiResponse;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mybatis-plus/user")
public class UserController {

    @Resource
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
//
//    @PostMapping("/query")
//    public ApiResponse<List<User>> query(@RequestBody BaseInfoQuery baseInfoQuery) {
//        List<User> list = dao.query(baseInfoQuery);
//        return ApiResponse.success(list);
//    }
//
//    @PostMapping("/queryPage")
//    public ApiResponse<Page<User>> queryPage(@RequestBody BaseInfoQuery baseInfoQuery) {
//        Page<User> page = dao.queryByPage(baseInfoQuery);
//        return ApiResponse.success(page);
//    }

}
