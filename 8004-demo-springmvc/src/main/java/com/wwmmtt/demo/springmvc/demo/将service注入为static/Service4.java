package com.wwmmtt.demo.springmvc.demo.将service注入为static;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Service4 {

    private static Service5 service5;

    //构造方式注入static属性
    @Autowired
    public Service4(Service5 service5) {
        Service4.service5 = service5;
    }
}
