package com.mmwwtt.demo.ee.fastjson;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class BaseInfoFastJsonVO {

    private Long baseInfoId;
    private String name;

    /**
     * 序列化后的时间格式化(json字符串传输)
     */
    @JSONField(format = "yyyy-MM-dd")
    private Date birthday;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date data1;
}
