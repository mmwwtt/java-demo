package com.wwmmtt.demo.springmvc.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/spring-mvc")
@Slf4j
public class SpringMVCController {
    @GetMapping("/get")
    public String get() {
        log.info("8004-get");
        return "spring-mvc-get";
    }
}
