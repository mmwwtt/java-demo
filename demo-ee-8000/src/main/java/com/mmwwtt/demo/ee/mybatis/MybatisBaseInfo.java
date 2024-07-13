package com.mmwwtt.demo.ee.mybatis;

import lombok.Data;

import java.util.Date;

@Data
public class MybatisBaseInfo {
    private String name;
    private String sexCode;
    private Date birthDate;
    private String employeeNumber;
    private String firstEducationCode;
    private String salary;

}
