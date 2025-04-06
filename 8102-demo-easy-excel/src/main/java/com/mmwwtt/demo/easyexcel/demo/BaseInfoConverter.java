package com.mmwwtt.demo.easyexcel.demo;

import com.mmwwtt.demo.mybatisplus.demo.BaseInfo;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Builder 是mapStruct包中的
@Mapper(builder = @Builder(disableBuilder = true))
public interface BaseInfoConverter {
    BaseInfoConverter INSTANCE = Mappers.getMapper(BaseInfoConverter.class );

    BaseInfoExcel toEasyExcelStudent(BaseInfo baseInfo);

    List<BaseInfoExcel> toEasyExcelStudent(List<BaseInfo> list);

}
