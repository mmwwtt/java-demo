package com.mmwwtt.demo.ee.aop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AopService {


    @Aspect1
    @Aspect2
    @Aspect3
    public String demoAnnotation() {
        log.info("aop demo");
        return "调用成功";
    }


}
