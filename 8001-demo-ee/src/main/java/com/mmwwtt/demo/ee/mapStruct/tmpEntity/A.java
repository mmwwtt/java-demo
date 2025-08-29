package com.mmwwtt.demo.ee.mapStruct.tmpEntity;

import com.mmwwtt.demo.common.entity.BaseInfo;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class A {
    private Map<String, Map<String, BaseInfo>> map;
    private List<Map<String, BaseInfo>> list;
}
