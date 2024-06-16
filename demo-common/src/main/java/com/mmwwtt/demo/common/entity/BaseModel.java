package com.mmwwtt.demo.common.entity;

import lombok.Data;

import java.util.Date;

@Data
public class BaseModel {
    public Long lastUpdatedBy;
    public Date lastUpdatedDate;

    public Long createdBy;
    public Date createdDate;
    public String deleteFlag;
}
