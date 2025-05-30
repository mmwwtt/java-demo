package com.mmwwtt.demo.easyexcel.demo;

import com.mmwwtt.demo.mybatisplus.demo.User;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface BaseInfoConverter {
    BaseInfoConverter INSTANCE = Mappers.getMapper(BaseInfoConverter.class );

    BaseInfoExcel toEasyExcelStudent(User user);

    List<BaseInfoExcel> toEasyExcelStudent(List<User> list);

}
