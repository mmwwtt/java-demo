package com.wwmmtt.demo.springmvc.demo.处理循环依赖;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Service2 {
    @Resource
    private Service1 service1;
}
