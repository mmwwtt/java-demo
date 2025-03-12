package com.mmwwtt.demo.fastjson.demo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mmwwtt.demo.common.response.ApiResponse;
import com.mmwwtt.demo.common.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/service/demo/ee/fastJson")
@Slf4j
public class FastJsonDemo {
    @PostMapping("/fastJsonDemo")
    public ApiResponse<List<BaseInfoFastJson>> fastJsonDemo(@RequestBody BaseInfoFastJson baseInfoFastJson) {
        log.info("{}", baseInfoFastJson);
        //JSON使用
        // java对象转JSON字符串
        String jsonStr = JSON.toJSONString(baseInfoFastJson);
        String jsonListStr = JSON.toJSONString(Arrays.asList(baseInfoFastJson));
        // JSON字符串转java对象
        BaseInfoFastJson baseInfo2 = JSON.parseObject(jsonStr, BaseInfoFastJson.class);
        //JSON字符串转List
        List<BaseInfoFastJson> list = JSON.parseArray(jsonListStr, BaseInfoFastJson.class);


        // JSONObject使用, 用键值对存储属性
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        Long baseInfoId = jsonObject.getLong("baseInfoId");
        jsonObject.remove("baseInfoId");
        jsonObject.put("fieldTest", "123");
        String jsonStr2 = JSONObject.toJSONString(jsonObject);

        // JOSNArray使用
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        JSONArray jsonArray1 = JSONArray.parseArray(jsonListStr);
        CommonUtils.println(baseInfo2, list, baseInfoId, jsonStr2, jsonArray1);
        return ApiResponse.success(list);
    }
}
