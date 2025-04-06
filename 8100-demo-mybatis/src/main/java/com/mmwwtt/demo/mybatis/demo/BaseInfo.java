package com.mmwwtt.demo.mybatis.demo;

import com.mmwwtt.demo.common.entity.Contact;
import com.mmwwtt.demo.common.entity.Family;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class BaseInfo {
    private Long baseInfoId;
    private String name;
    private String sexCode;
    private Double height;
    private LocalDate birthDate;
    private Contact contact;
    private List<Family> familyList;

}
