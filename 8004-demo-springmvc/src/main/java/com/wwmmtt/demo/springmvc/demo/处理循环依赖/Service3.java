package com.wwmmtt.demo.springmvc.demo.处理循环依赖;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Service3 {

    @Autowired
    private Service1 service1;


    public void setService1(Service1 service1) {
        this.service1 = service1;
    }

}
