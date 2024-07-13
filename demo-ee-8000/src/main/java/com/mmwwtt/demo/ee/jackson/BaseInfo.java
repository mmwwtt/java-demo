package com.mmwwtt.demo.ee.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class BaseInfo {

    private Long baseInfoId;
    private String name;

    private Date birthday;

    //json序列化时转为指定格式
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    //接收时转为指定格式
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date data1;

}
