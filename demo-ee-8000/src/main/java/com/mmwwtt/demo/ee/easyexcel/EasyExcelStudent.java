package com.mmwwtt.demo.ee.easyexcel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EasyExcelStudent {
    @ExcelProperty(value = "姓名")
    private String name;
    @ExcelProperty(value = "学号")
    private String studentNumber;

    @ExcelProperty(value = "生日")
    private LocalDateTime birthDay;
}
