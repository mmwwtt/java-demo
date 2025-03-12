package com.mmwwtt.demo.ee.mapStruct使用;

import com.mmwwtt.demo.common.dto.BaseInfoDTO;
import com.mmwwtt.demo.common.dto.ContactDTO;
import com.mmwwtt.demo.common.entity.BaseInfo;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Mapper(builder = @Builder(disableBuilder = true))
public interface BaseInfoConverter {
    BaseInfoConverter INSTANCE = Mappers.getMapper( BaseInfoConverter.class );
    @Mappings({
            @Mapping(target = "name", source = "name"),
            @Mapping(target = "address", source = "address.address"),
            @Mapping(target = "phoneNumber", source = "contactList", qualifiedByName = "getPhoneNumber"),
            @Mapping(target = "salary", ignore = true),
            //@Mapping(expression = "java(java.time.LocalDateTime.now())", target = "nowTime")
    })
    BaseInfo converter(BaseInfoDTO baseInfoDTO);

    List<BaseInfo> converter(List<BaseInfoDTO> list);

    @Named("getPhoneNumber")
    default String getPhoneNumber(List<ContactDTO> contactList) {
        AtomicReference<String> phoneNumber = new AtomicReference<>("");
        contactList.stream().forEach(item -> {
            if ("".equals(item.getType())) {
                phoneNumber.set(item.getContactInfo());
            }
        });
        return phoneNumber.get();
    }
}
