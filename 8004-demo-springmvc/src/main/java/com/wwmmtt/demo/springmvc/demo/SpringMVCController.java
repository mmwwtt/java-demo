package com.wwmmtt.demo.springmvc.demo;

import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spring-mvc")
@Slf4j
public class SpringMVCController {
    @Resource
    private LttRedisProperties lttRedisProperties;

    @GetMapping("/get")
    public String get() {
        log.info("8004-get");
        return "spring-mvc-get";
    }

    @GetMapping("/get/{id}")
    public void getParam(@RequestParam("name") String name,
                         @PathVariable("id") String productId,
                         HttpServletRequest request) {
        String xToken = request.getHeader("X-token");
    }

    @PostMapping("/get")
    public void getParam(@RequestBody BaseInfoDTO baseInfoDTO) {
    }
}
