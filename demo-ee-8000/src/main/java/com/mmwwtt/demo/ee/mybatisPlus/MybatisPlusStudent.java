package com.mmwwtt.demo.ee.mybatisPlus;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("student_t")
public class MybatisPlusStudent {
    private Long studentId;
    private String name;
}
