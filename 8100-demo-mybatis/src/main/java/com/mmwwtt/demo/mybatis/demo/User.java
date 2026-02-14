package com.mmwwtt.demo.mybatis.demo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mmwwtt.demo.common.entity.Contact;
import com.mmwwtt.demo.common.entity.Family;
import lombok.Data;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ext.javatime.deser.LocalDateDeserializer;
import tools.jackson.databind.ext.javatime.ser.LocalDateSerializer;

import java.time.LocalDate;
import java.util.List;

@Data
public class User {
    private Long userId;
    private String name;
    private String sexCode;
    private Double height;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    private LocalDate birthDate;

    private Contact contact;
    private List<Family> familyList;

}
