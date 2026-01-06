package com.mmwwtt.demo.ee.mapStruct;

import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.entity.Contact;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true),
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE   //为null的字段不进行拷贝(不会把老值改成null)
)
public interface BaseInfoConverter {
    BaseInfoConverter INSTANCE = Mappers.getMapper( BaseInfoConverter.class );

    /**
     * mapStruct 默认是浅拷贝， 类里面的对象属性(list,map,set,自定义对象)都是直接浅拷贝了引用
     * 需要对每个引用类型都增加converter方法
     */
    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "email", source = "contact.email"),
            @Mapping(target = "phoneNumber", source = "contact", qualifiedByName = "getPhoneNumber"),
            @Mapping(target = "deleteFlag", ignore = true),
            @Mapping(expression = "java(java.time.LocalDateTime.now())", target = "nowTime")
    })
    BaseInfoDTO converterToDTO(BaseInfo baseInfo);

    List<BaseInfoDTO> converterToDTO(List<BaseInfo> list);

    @Named("getPhoneNumber")
    default String getPhoneNumber(Contact contact) {
        return contact.getPhoneNumber();
    }

    /**
     * 所有方法结束后都执行该方法
     * @MappingTarget 编辑目标对象
     * @return
     */
    @AfterMapping
    default void fun1(@MappingTarget BaseInfoDTO dto) {
        dto.setBaseInfoId(dto.getBaseInfoId());
    }
}
