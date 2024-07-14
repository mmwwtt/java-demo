package com.mmwwtt.demo.ee.mybatisPlus;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

@Data
@TableName("student_t")
public class MybatisPlusStudentQuery extends Page {

    @TableId(type = IdType.AUTO)
    private Long studentId;
    private String name;

}
