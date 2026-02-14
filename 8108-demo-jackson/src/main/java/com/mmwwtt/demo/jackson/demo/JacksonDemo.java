package com.mmwwtt.demo.jackson.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmwwtt.demo.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/service/demo/ee/jackson")
@Slf4j
public class JacksonDemo {
    @PostMapping("/JacksonDemo")
    public ApiResponse<BaseInfoJackson> fastJsonDemo(@RequestBody BaseInfoJackson baseInfoJackson) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(baseInfoJackson);
        BaseInfoJackson vo = objectMapper.readValue(jsonStr, BaseInfoJackson.class);
        return ApiResponse.success(vo);
    }

    @Test
    public void test1() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> map = new HashMap<>();
        map.put("name", "Tom");
        String jsonString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        BaseInfoJackson baseInfo = mapper.readValue(jsonString, BaseInfoJackson.class);
        log.info(baseInfo.toString());
    }
}
