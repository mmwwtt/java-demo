package com.wwmmtt.demo.springmvc.demo;

import com.mmwwtt.demo.common.util.SpringContextUtil;

public interface DemoService {

    default void fun() {

        //在接口中注入一个bean
        SpringMVCController bean = SpringContextUtil.getBean(SpringMVCController.class);
    }
}
