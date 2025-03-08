package com.mmwwtt.demo.ee.json.fastjson;

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
    public ApiResponse<List<BaseInfoFastJsonVO>> fastJsonDemo(@RequestBody BaseInfoFastJsonVO baseInfoFastJsonVO) {
        log.info("{}",baseInfoFastJsonVO);
        //JSON使用
        // java对象转JSON字符串
        String jsonStr = JSON.toJSONString(baseInfoFastJsonVO);
        String jsonListStr = JSON.toJSONString(Arrays.asList(baseInfoFastJsonVO));
        // JSON字符串转java对象
        BaseInfoFastJsonVO baseInfo2 = JSON.parseObject(jsonStr, BaseInfoFastJsonVO.class);
        //JSON字符串转List
        List<BaseInfoFastJsonVO> list = JSON.parseArray(jsonListStr, BaseInfoFastJsonVO.class);


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
