package com.mmwwtt.demo.ee.validation;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDate;
import java.util.List;

@Data
public class BaseInfoValidation {
    @NotEmpty(message = "名称不能为空")
    @Size(min = 1, max = 10, message = "名字要在1-10个字符内")
    private String name;
    @NotEmpty(message = "性别不能为空")
    private String sexCode;

    @NotNull(message = "出生年月日不能为空")
    private LocalDate birthDate;

    @NotEmpty(message = "集合不能为null，且不能为空")
    private List<Long> list;

    @Min(value= 0, message = "身高需要在0-300范围内")
    @Max(value= 300, message = "身高需要在0-300范围内")
    @NotNull(message = "身高不能为null")
    @Range(min = 0, max = 300, message = "身高需要在0-300范围内")
    private Double height;

    @NotNull(message = "体重不能为null")
    @Range(min = 0, max = 300, message = "体重需要在0-300范围内")
    private Double weight;

    @Email(message = "邮件格式不正确")
    private String email;

}
