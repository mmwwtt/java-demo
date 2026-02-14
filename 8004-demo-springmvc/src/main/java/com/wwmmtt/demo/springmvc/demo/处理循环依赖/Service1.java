package com.wwmmtt.demo.springmvc.demo.处理循环依赖;

import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class Service1 {

    //解决方式1：延迟加载
    @Lazy
    @Resource
    private Service2 service2;

    private Service3 service3;

    //解决方式2：set方式注入
    public void setService3(Service3 service3) {
        this.service3 = service3;
    }

}
