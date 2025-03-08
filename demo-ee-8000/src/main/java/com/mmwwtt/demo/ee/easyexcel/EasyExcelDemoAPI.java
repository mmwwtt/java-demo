package com.mmwwtt.demo.ee.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.mmwwtt.demo.common.response.ApiResponse;
import com.mmwwtt.demo.ee.mybatisPlus.MybatisPlusStudent;
import com.mmwwtt.demo.ee.mybatisPlus.MybatisPlusStudentMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/service/demo/ee/easyExcel")
@Slf4j
public class EasyExcelDemoAPI {
    @Resource
    private MybatisPlusStudentMapper studentMapper;

    StudentConverter studentConverter = StudentConverter.INSTANCE;

    @PostMapping("/exportDemo")
    public ApiResponse<Void> exportDemo(HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {
        List<MybatisPlusStudent> list = studentMapper.selectByQuery(null);
        List<EasyExcelStudent> excelList = studentConverter.toEasyExcelStudent(list);
        String filePath = "D:\\test\\excel\\student_export.xlsx";
        File file = new File(filePath);

        Set<String> excludeField = new HashSet<>();
        excludeField.add("hireDate");
        excludeField.add("salary");


        /**
         * writer 文件路径  和 表头映射
         * excludeColumnFiledNames:需要排除的字段， 可以在字段上加@ExcelIgnore注解排除
         */
        EasyExcel.write(file, EasyExcelStudent.class)
                .excludeColumnFiledNames(excludeField)
                .sheet("学生信息")
                .doWrite(excelList);
        //将file文件保存到文件服务器 自行完善

        //直接返回前端文件
        EasyExcel.write(response.getOutputStream(), EasyExcelStudent.class)
                .excludeColumnFiledNames(excludeField)
                .sheet("学生信息")
                .doWrite(excelList);
       return ApiResponse.success();
    }

    @PostMapping("/importDemo")
    public ApiResponse<Void> importDemo() {
        // 读取的excel文件路径
        String filename = "D:\\test\\excel\\student_export.xlsx";
        List<EasyExcelStudent> list = new ArrayList<>();
        // 读取excel
        EasyExcel.read(filename, EasyExcelStudent.class, new AnalysisEventListener<EasyExcelStudent>() {
            // 每解析一行数据,该方法会被调用一次
            @Override
            public void invoke(EasyExcelStudent demoData, AnalysisContext analysisContext) {
                list.add(demoData);
            }

            // 全部解析完成被调用
            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                log.info("解析完成...");
                // 可以将解析的数据保存到数据库 save(list)
            }
        }).sheet().doRead();
        return ApiResponse.success();
    }
}
