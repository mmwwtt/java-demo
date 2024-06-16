package com.mmwwtt.demo.common.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BaseInfo extends BaseModel{
    private Long baseInfoId;
    private String name;
    private String sexCode;
    private Date birthDate;

    private String employeeNumber;

    private String firstEducationCode;


    private String salary;
}
