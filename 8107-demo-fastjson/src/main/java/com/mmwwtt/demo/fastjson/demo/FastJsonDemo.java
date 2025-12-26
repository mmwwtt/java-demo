package com.mmwwtt.demo.fastjson.demo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.response.ApiResponse;
import com.mmwwtt.demo.common.util.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
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


        JSONObject obj = JSONObject.parseObject(jsonListStr);
        List<BaseInfoFastJson> listtt = obj.toJavaObject(List.class);

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
        object.put("k", "");
        JSONObject object1 = new JSONObject();
        object.put("k", null);
        Integer k1 = object.getInteger("k");
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

    @Test
    public void test4() {
        JSONObject obj = JSONObject.parseObject(JSONObject.toJSONString(BaseInfo.getPresetList()));
        List<BaseInfoFastJson> listtt = obj.toJavaObject(List.class);
        System.out.println();
    }

    /**
     * fastJson序列化时会忽略null值
     * WriteMapNullValue  将为null的值继续序列化
     */
    @Test
    public void test5() {
        BaseInfo baseInfo = new BaseInfo();
        String json1 = JSONObject.toJSONString(baseInfo);
        String json2 = JSONObject.toJSONString(baseInfo, JSONWriter.Feature.WriteMapNullValue);

        System.out.println(json1);
        System.out.println(json2);
    }

    @Test
    public void test6() {
        JSONObject json = new JSONObject();
        json.put("name", "tt");
        json.put("age", 12);

        JSONObject json2 = new JSONObject();
        json2.put("age", 12);
        json2.put("name", "tt");
        String json11 = JSONObject.toJSONString(json);
        String json22 = JSONObject.toJSONString(json2);

        System.out.println();
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
