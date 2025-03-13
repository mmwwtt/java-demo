package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("base_info_t")
public class BaseInfoMyBatisPlus {

    @TableId(type = IdType.AUTO)
    private Long baseInfoId;
    private String name;

    private String sexCode;

    private Double height;

    private LocalDate birthDate;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastUpdatedDate;
}
