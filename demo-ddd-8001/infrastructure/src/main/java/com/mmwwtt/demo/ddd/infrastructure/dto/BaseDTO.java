package com.mmwwtt.demo.ddd.infrastructure.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseDTO {
    private Date createDate;

    private Long createById;

    private String createByName;

    private Date lastUpdateDate;

    private Long lastUpdateById;

    private String lastUpdateByName;



}
