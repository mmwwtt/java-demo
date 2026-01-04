package com.mmwwtt.demo.fastjson.demo;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    /**
     * 避免精度丢失  前端16位   Long最长有19位
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long baseInfoId;

    private String name;

    @JSONField(format = "yyyy-MM-dd")
    private Date birthDate;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date birthDateTime;

}
