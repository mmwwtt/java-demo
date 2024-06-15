package com.mmwwtt.demo.ee.demo.domain.interfaces;

import com.mmwwtt.demo.ee.validation使用.ValidationBaseInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ee/demo")
public class DemoAPI {
    @PostMapping("/validation")
    public void demoValidation(ValidationBaseInfo validationBaseInfo) {

    }

    public static void main(String[] args) {
        String str = "[].[]";
        Object object = str.split(".");
        String strt = str.substring(1,1);
        System.out.println(object);
    }
}
