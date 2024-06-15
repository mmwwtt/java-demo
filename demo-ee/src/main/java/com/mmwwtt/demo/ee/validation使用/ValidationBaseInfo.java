package com.mmwwtt.demo.ee.validation使用;

import jakarta.validation.constraints.*;

import java.util.Date;

public class ValidationBaseInfo {

    private Long baseInfoId;

    @NotEmpty
    private String employeeNumber;

    @NotNull
    private String education;

    @Size(min = 0, max = 10000000)
    private Integer salary;

    @Max(value = 1000)
    private Integer number1;

    @Min(value = 0)
    private Integer number2;

    private String name;

    private Date birthday;
}
