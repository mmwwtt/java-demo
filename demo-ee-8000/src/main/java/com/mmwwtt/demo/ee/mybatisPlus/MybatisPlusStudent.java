package com.mmwwtt.demo.ee.mybatisPlus;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.util.Date;

@Data
@TableName("student_t")
public class MybatisPlusStudent {

    @TableId(type = IdType.AUTO)
    private Long studentId;
    private String name;

    @TableField(fill = FieldFill.INSERT)
    private Date createdDate;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date lastUpdatedDate;
}
