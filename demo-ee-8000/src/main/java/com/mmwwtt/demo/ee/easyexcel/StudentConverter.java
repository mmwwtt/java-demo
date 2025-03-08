package com.mmwwtt.demo.ee.easyexcel;

import com.mmwwtt.demo.ee.mybatisPlus.MybatisPlusStudent;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

//@Builder 是mapStruct包中的
@Mapper(builder = @Builder(disableBuilder = true))
public interface StudentConverter {
    StudentConverter INSTANCE = Mappers.getMapper(StudentConverter.class );

    EasyExcelStudent toEasyExcelStudent(MybatisPlusStudent student);

    List<EasyExcelStudent> toEasyExcelStudent(List<MybatisPlusStudent> list);
}
