package com.mmwwtt.demo.ee.validation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/demo/ee")
public class ValidationDemo {
    @PostMapping("/validation")
    public void demoValidation(BaseInfoCreateDTO baseInfoCreateDTO) {
        System.out.println(baseInfoCreateDTO);
    }

    public static void main(String[] args) {
        String str = "[].[]";
        Object object = str.split(".");
        String strt = str.substring(1,1);
        System.out.println(object);
    }
}
