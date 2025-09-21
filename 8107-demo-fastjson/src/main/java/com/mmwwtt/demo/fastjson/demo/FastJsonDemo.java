package com.mmwwtt.demo.fastjson.demo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.mmwwtt.demo.common.response.ApiResponse;
import com.mmwwtt.demo.common.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
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
        JSONObject jsonObject1 = new JSONObject();
        JSONObject jsonObject = JSONObject.parseObject(jsonStr);
        Long baseInfoId = jsonObject.getLong("baseInfoId");
        jsonObject.remove("baseInfoId");
        jsonObject.put("fieldTest", "123");
        String jsonStr2 = JSONObject.toJSONString(jsonObject);

        // JOSNArray使用
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(jsonObject);
        JSONArray jsonArray1 = JSONArray.parseArray(jsonListStr);
        jsonObject.put("array", jsonArray1);
        CommonUtils.println(baseInfo2, list, baseInfoId, jsonStr2, jsonArray1);
        return ApiResponse.success(list);
    }

    @Test
    public void test1() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonArray.add("hello");
        jsonObject.put("array", jsonArray);
        log.info(jsonObject.toJSONString());
    }

    @Test
    public void test2() {
        JSONObject object = new JSONObject();
        object.put("k","");
        JSONObject object1 = new JSONObject();
        object.put("k",null);
        Integer k1= object.getInteger("k");
        Integer k = object1.getInteger("k");
        System.out.println();
    }

    @Test
    public void test3() {
        JSONObject json = new JSONObject();
        json.put("map", new HashMap<>());
        Object map = json.get("map");
        //JSONObject mapJson = (JSONObject) map;  //不能直接转换，因为map中是Map结构，无法转成JSONObject
        JSONObject mapJson = (JSONObject) JSON.toJSON(map);  //toJSON 将内部结构转成JSON后再进行强转
    }
}
