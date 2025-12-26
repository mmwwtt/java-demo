package com.wwmmtt.demo.springmvc.demo;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import com.mmwwtt.demo.common.entity.多态.Animal;
import com.mmwwtt.demo.common.response.ApiResponse;
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

    @PostMapping("/fastJsonDemo1")
    public ApiResponse<JSONArray> fastJsonDemo1() {
        JSONArray array = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("name", "tt");
        json.put("age", 12);

        JSONObject json2 = new JSONObject();
        json2.put("age", 12);
        json2.put("name", "tt");
        array.add(json);
        array.add(json2);
        return ApiResponse.success(array);
    }
}
