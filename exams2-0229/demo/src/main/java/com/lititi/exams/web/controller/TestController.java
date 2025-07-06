package com.lititi.exams.web.controller;

import com.lititi.exams.commons2.log.LttLogger;
import com.lititi.exams.commons2.object.CommonResultObject;
import com.lititi.exams.web.service.TestDemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 演示文件，请勿在此文件添加或者修改代码！！！
 * 演示文件，请勿在此文件添加或者修改代码！！！
 * 演示文件，请勿在此文件添加或者修改代码！！！
 */

@RestController
@RequestMapping("/test")
public class TestController extends BaseController{

    @Autowired
    private TestDemoService testDemoService;

    private static final LttLogger logger = new LttLogger(TestController.class);

    @GetMapping("/contentDetail")
    public CommonResultObject getContentByNumber(Integer number){
        CommonResultObject resultObject = new CommonResultObject();
        resultObject.addObject("content", testDemoService.selectByNumber(number));
        return resultObject;
    }

    @GetMapping("/hello")
    public CommonResultObject sayHello(String word){

        CommonResultObject resultObject = new CommonResultObject();
        resultObject.addObject("reply", word + " too!");
        return resultObject;
    }

}
