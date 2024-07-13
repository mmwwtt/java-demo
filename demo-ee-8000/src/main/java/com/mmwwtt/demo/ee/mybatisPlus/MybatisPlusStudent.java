package com.mmwwtt.demo.ee.mybatisPlus;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student_t")
public class MybatisPlusStudent {

    @TableId(type = IdType.AUTO)
    private Long studentId;
    private String name;
}
