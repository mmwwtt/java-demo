package com.mmwwtt.demo.jackson.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmwwtt.demo.common.response.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/demo/ee/jackson")
public class JacksonDemo {
    @PostMapping("/JacksonDemo")
    public ApiResponse<BaseInfoJackson> fastJsonDemo(@RequestBody BaseInfoJackson baseInfoJackson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(baseInfoJackson);
        BaseInfoJackson vo = objectMapper.readValue(jsonStr, BaseInfoJackson.class);
        return ApiResponse.success(vo);
    }
}
