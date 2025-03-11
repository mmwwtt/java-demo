package com.mmwwtt.demo.easyexcel.demo;

import com.mmwwtt.demo.mybatisplus.demo.BaseInfoMyBatisPlus;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Builder 是mapStruct包中的
@Mapper(builder = @Builder(disableBuilder = true))
public interface BaseInfoConverter {
    BaseInfoConverter INSTANCE = Mappers.getMapper(BaseInfoConverter.class );

    BaseInfoExcel toEasyExcelStudent(BaseInfoMyBatisPlus baseInfoMyBatisPlus);

    List<BaseInfoExcel> toEasyExcelStudent(List<BaseInfoMyBatisPlus> list);

}
