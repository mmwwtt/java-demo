package com.mmwwtt.demo.common.converter;

import com.mmwwtt.demo.common.entity.Contact;
import com.mmwwtt.demo.common.vo.ContactVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface ContactConverter {
    ContactConverter INSTANCE = Mappers.getMapper(ContactConverter.class);

    Contact converterToEntity(ContactVO vo);

    ContactVO converterToVO(Contact entity);

    List<Contact> converterToEntity(List<ContactVO> list);

    List<ContactVO> converterToVO(List<Contact> list);
}
