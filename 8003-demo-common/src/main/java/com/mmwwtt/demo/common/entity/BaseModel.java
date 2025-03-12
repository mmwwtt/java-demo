package com.mmwwtt.demo.common.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BaseModel {
    public Long lastUpdatedBy;
    public LocalDateTime lastUpdatedDate;

    public Long createdBy;
    public LocalDateTime createdDate;
    public String deleteFlag;
}
