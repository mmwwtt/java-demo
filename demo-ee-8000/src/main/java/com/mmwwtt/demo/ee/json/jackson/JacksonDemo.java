package com.mmwwtt.demo.ee.json.jackson;

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
    public ApiResponse<BaseInfoJacksonVO> fastJsonDemo(@RequestBody  BaseInfoJacksonVO baseInfoJacksonVO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonStr = objectMapper.writeValueAsString(baseInfoJacksonVO);
        BaseInfoJacksonVO vo = objectMapper.readValue(jsonStr, BaseInfoJacksonVO.class);
        return ApiResponse.success(vo);
    }
}
