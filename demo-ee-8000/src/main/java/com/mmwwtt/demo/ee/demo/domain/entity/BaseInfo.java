package com.mmwwtt.demo.ee.demo.domain.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BaseInfo {
    private Long baseInfoId;
    private String name;

    private Date birthday;

}
