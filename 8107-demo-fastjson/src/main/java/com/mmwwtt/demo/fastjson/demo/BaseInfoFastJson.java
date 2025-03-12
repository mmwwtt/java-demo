package com.mmwwtt.demo.fastjson.demo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseInfoFastJson {

    private Long baseInfoId;
    private String name;

    @JSONField(format = "yyyy-MM-dd")
    private Date birthday;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date birthDateTime;
}
