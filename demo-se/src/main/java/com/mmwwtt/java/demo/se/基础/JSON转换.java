package com.mmwwtt.java.demo.se.基础;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmwwtt.demo.common.entity.BaseInfo;
import org.junit.jupiter.api.Test;

public class JSON转换 {
    @Test
    public void json_to_entity_test() {
        BaseInfo baseInfo = JSONObject.parseObject(getBaseInfoJson(), BaseInfo.class);
        System.out.println(baseInfo.toString());
    }

    @Test
    public void json_to_entity() throws JsonProcessingException, ClassNotFoundException {
        Class entityClass = Class.forName("com.mmwwtt.demo.common.entity.BaseInfo");
        ObjectMapper mapper = new ObjectMapper();
        BaseInfo baseInfo1 = mapper.readValue(getBaseInfoJson(), BaseInfo.class);
        BaseInfo baseInfo2 = (BaseInfo) mapper.readValue(getBaseInfoJson(), entityClass );
        return;
    }

    public String getBaseInfoJson() {
        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setName("小明");
        baseInfo.setBaseInfoId(1L);
        return JSON.toJSONString(baseInfo);
    }
}
