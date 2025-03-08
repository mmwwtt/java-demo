package com.mmwwtt.demo.ee.mybatisPlus;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("student_t")
public class MybatisPlusStudent {

    @TableId(type = IdType.AUTO)
    private Long studentId;
    private String name;

    private String studentNumber;

    private LocalDateTime birthDay;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime lastUpdatedDate;
}
