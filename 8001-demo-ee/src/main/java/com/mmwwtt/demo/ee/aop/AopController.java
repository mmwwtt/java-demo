package com.mmwwtt.demo.ee.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/aop")
public class AopController {

    @GetMapping("/demo")
    public String demo0() {
      log.info("aop demo");
      return "调用成功";
    }



    @Aspect1
    @Aspect2
    @Aspect3
    @GetMapping("/demo1/annotation")
    public String demo1Annotation() {
        return "调用成功";
    }

    @Aspect1
    @Aspect2
    @Aspect3
    @GetMapping("/demo2/annotation")
    public String demo2Annotation() {
        return "调用成功";
    }
}
