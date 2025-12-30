package com.mmwwtt.demo.ee.aop;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/aop")
public class AopController {

    @Resource
    private AopService aopService;

    @GetMapping("/demo")
    public String demo() {
      log.info("aop demo");
      return "调用成功";
    }

    @GetMapping("/demo/annotation")
    public String demoAnnotation() {
        aopService.demoAnnotation();
        return "调用成功";
    }
}
