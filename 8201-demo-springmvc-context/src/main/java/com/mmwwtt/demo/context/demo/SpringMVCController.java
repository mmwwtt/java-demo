package com.mmwwtt.demo.context.demo;

import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spring-mvc")
@Slf4j
public class SpringMVCController {

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

    @PostMapping("/getParam1")
    public void getParam1(BaseInfoDTO baseInfoDTO) {
        log.info(baseInfoDTO.toString());
    }

}
