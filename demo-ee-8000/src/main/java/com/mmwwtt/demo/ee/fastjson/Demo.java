package com.mmwwtt.demo.ee.fastjson;

import com.alibaba.fastjson2.JSON;
import com.mmwwtt.demo.common.util.CommonUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class Demo {
    @Test
    public void test() {
        BaseInfoFastJsonVO baseInfo = BaseInfoFastJsonVO.builder().baseInfoId(1L).build();
        // java对象转JSON字符串
        String jsonStr = JSON.toJSONString(baseInfo);

        // List转JSON字符串
        String jsonListStr = JSON.toJSONString(Arrays.asList(baseInfo));


        // JSON字符串转java对象
        BaseInfoFastJsonVO baseInfo2 =  JSON.parseObject(jsonStr, BaseInfoFastJsonVO.class);

        //JSON字符串转List
        List<BaseInfoFastJsonVO> list = JSON.parseArray(jsonListStr, BaseInfoFastJsonVO.class);
        CommonUtils.println(baseInfo2, list);
    }
}
