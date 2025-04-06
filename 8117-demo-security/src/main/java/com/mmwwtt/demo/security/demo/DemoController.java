package com.mmwwtt.demo.security.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/security")
public class DemoController {

    @GetMapping("/getUserInfo")
    public void getUserInfo() {
    }
}

