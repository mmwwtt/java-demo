package com.mmwwtt.demo.mybatis.demo;

import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mybatis/user")
@Slf4j
public class UserController {


    @Resource
    private UserService userService;

    @PostMapping("/query")
    public List<User> query(@RequestBody User query) {
        return this.userService.query(query);
    }

    @PostMapping("/queryPage")
    public PageInfo<User> queryPage(@RequestBody User user,
                                    @RequestParam("pageNum") int pageNum,
                                    @RequestParam("pageSize") int pageSize) {
        return userService.queryPage(user, pageNum, pageSize);
    }

    @PostMapping("/save")
    public User save(@RequestBody User user) {
        return userService.save(user);
    }

    @PostMapping("/saveAll")
    public List<User> saveAll(@RequestBody List<User> list) {
        return userService.saveAll(list);
    }

    @PostMapping("/update")
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    @PostMapping("/updateAll")
    public List<User> updateAll(@RequestBody List<User> list) {
        return userService.updateAll(list);
    }

}
