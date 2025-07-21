package com.mmwwtt.demo.easyexcel.demo;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.mmwwtt.demo.common.response.ApiResponse;
import com.mmwwtt.demo.easyexcel.demo.config.I18nEasyExcelHandler;
import com.mmwwtt.demo.easyexcel.demo.dao.UserDao;
import com.mmwwtt.demo.mybatisplus.demo.User;
import com.mmwwtt.demo.mybatisplus.demo.UserQuery;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
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
@RequestMapping("/easyExcel")
@Slf4j
public class EasyExcelController {
    @Resource
    private UserDao userDao;

    @Resource
    private I18nEasyExcelHandler i18nEasyExcelHandler;

    BaseInfoConverter baseInfoConverter = BaseInfoConverter.INSTANCE;

    @PostMapping("/exportDemo")
    public ApiResponse<Void> exportDemo(HttpServletRequest request,
                                        HttpServletResponse response) throws IOException {

        //国际化标志
        String language = LocaleContextHolder.getLocale().getLanguage();

        List<User> list = userDao.queryList(null);
        List<BaseInfoExcel> excelList = baseInfoConverter.toEasyExcelStudent(list);
        String filePath = "D:\\student_export.xlsx";
        File file = new File(filePath);

        Set<String> excludeField = new HashSet<>();
        excludeField.add("hireDate");
        excludeField.add("salary");


        /**
         * writer 文件路径  和 表头映射
         * excludeColumnFiledNames:需要排除的字段， 可以在字段上加@ExcelIgnore注解排除
         */
        EasyExcel.write(file, BaseInfoExcel.class)
                .registerWriteHandler(i18nEasyExcelHandler)   // 注册翻译器
                .excludeColumnFiledNames(excludeField)
                .sheet("学生信息")
                .doWrite(excelList);
        //将file文件保存到文件服务器 自行完善

        //直接返回前端文件
        EasyExcel.write(response.getOutputStream(), BaseInfoExcel.class)
                .registerWriteHandler(i18nEasyExcelHandler)   // 注册翻译器
                .excludeColumnFiledNames(excludeField)
                .sheet("学生信息")
                .doWrite(excelList);

        try (ExcelWriter writer = EasyExcel.write(response.getOutputStream(), User.class).build()) {
            WriteSheet sheet = EasyExcel.writerSheet("用户数据").build();
            UserQuery query = new UserQuery();
            query.setCurrent(1);
            query.setSize(1000);
            while (true) {

                List<BaseInfoExcel> rows =  baseInfoConverter.toEasyExcelStudent(userDao.queryPage(query).getRecords()); // 自己写的分页
                if (rows.isEmpty()) break;
                writer.write(rows, sheet);
                if (rows.size() < query.getSize()) break;   // 最后一页
                query.setCurrent(query.getCurrent());
            }
        }
       return ApiResponse.success();
    }

    @PostMapping("/importDemo")
    public ApiResponse<Void> importDemo() {
        // 读取的excel文件路径
        String filename = "D:\\test\\excel\\student_export.xlsx";
        List<BaseInfoExcel> list = new ArrayList<>();
        // 读取excel
        EasyExcel.read(filename, BaseInfoExcel.class, new AnalysisEventListener<BaseInfoExcel>() {
            // 每解析一行数据,该方法会被调用一次
            @Override
            public void invoke(BaseInfoExcel demoData, AnalysisContext analysisContext) {
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
