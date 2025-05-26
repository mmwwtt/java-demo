package com.mmwwtt.demo.mybatisplus.demo.permission;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/mybatis-plus/permission")
public class BaseInfoController {

    @Resource
    private BaseInfoPermissDao baseInfoPermissDao;


    @PostMapping("getBaseInfoList")
    public List<BaseInfo> getBaseInfoList() {
        return baseInfoPermissDao.getBaseInfoList();
    }
}