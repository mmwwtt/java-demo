package com.wwmmtt.demo.springmvc.demo;

import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import com.mmwwtt.demo.common.entity.animal.Animal1;
import com.mmwwtt.demo.common.entity.多态.Animal;
import com.wwmmtt.demo.springmvc.BaseInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/getParam")
    public void getParam(@RequestBody BaseInfo baseInfo) {
        System.out.println();
    }

    @PostMapping("/getParam1")
    public void getParam1(BaseInfoDTO baseInfoDTO) {
        log.info(baseInfoDTO.toString());
    }

    /**
     *
     */
    @PostMapping("/testRequestBody")
    public BaseInfo voidTestRequestBody(@RequestBody BaseInfo baseInfo){
        BaseInfo baseInfo1 = new BaseInfo();
        System.out.println(baseInfo);
        return baseInfo;
    }

    /**
     * 用父类list接收不同的子类
     */
    @PostMapping("/testSonClassList")
    public void voidTestRequestBody(@RequestBody List<Animal> list){
        System.out.println(list);
    }

    /**
     * 用父类对象接收子类对象
     */
    @PostMapping("/testSonClass")
    public void voidTestRequestBody(@RequestBody Animal1 animal){
        System.out.println(animal);
    }
}
