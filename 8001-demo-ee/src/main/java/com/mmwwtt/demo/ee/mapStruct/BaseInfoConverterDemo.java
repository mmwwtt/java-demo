package com.mmwwtt.demo.ee.mapStruct;

import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.entity.Contact;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface BaseInfoConverterDemo {
    BaseInfoConverterDemo INSTANCE = Mappers.getMapper( BaseInfoConverterDemo.class );

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
}
