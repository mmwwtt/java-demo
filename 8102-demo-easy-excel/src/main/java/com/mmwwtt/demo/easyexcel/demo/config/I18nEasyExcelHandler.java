package com.mmwwtt.demo.easyexcel.demo.config;

import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.mmwwtt.demo.easyexcel.demo.I18nUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用于EasyExcel中导出的表头国际化
 */
@Component
@RequiredArgsConstructor
public class I18nEasyExcelHandler implements CellWriteHandler {

    @Autowired
    private I18nUtils i18nUtils;

    @Override
    public void beforeCellCreate(CellWriteHandlerContext context) {
        if (!context.getHead()) return;

        List<String> origin = context.getHeadData().getHeadNameList();
        if (CollectionUtils.isEmpty(origin)) return;

        List<String> translated = origin.stream()
                .map(i18Path-> i18nUtils.get(i18Path))
                .collect(Collectors.toList());

        context.getHeadData().setHeadNameList(translated);
    }
}
