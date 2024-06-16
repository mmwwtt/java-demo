package com.mmwwtt.demo.ee.validation;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class BaseInfoCreateDTO{
    @NotEmpty(message = "名称不能为空")
    private String name;
    private String sexCode;
    private Date birthDate;
    @NotEmpty(message = "工号不能为空")
    private String employeeNumber;
    @NotEmpty(message = "第一学历不能为空")
    private String firstEducationCode;

    @Size(min = 0, max = 10000000, message = "薪资需要在0-10000000范围内")
    private String salary;

}
