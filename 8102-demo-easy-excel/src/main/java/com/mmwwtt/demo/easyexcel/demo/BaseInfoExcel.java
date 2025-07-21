package com.mmwwtt.demo.easyexcel.demo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BaseInfoExcel {

    @ExcelProperty(value = "excel.name")
    private String name;

    @ExcelProperty(value = "excel.sutdentNumber")
    private String studentNumber;

}
