package com.mmwwtt.demo.ee.demo.domain.vo;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;

public class BaseInfoVO {

    private Long baseInfoId;
    private String name;

    /**
     * 序列化后的时间格式化(json字符串传输)
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date birthday;
}
