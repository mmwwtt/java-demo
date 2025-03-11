package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("base_info_t")
public class BaseInfoMyBatisPlus {

    @TableId(type = IdType.ASSIGN_UUID)
    private String baseInfoId;
    private String name;

    private String sex;

    private Double height;

    private LocalDate birthDay;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastUpdatedDate;
}
