package com.mmwwtt.demo.ddd.infrastructure.converter;

import com.mmwwtt.demo.ddd.infrastructure.dto.lookuptype.LookupTypeDTO;
import com.mmwwtt.demo.ddd.domain.entity.LookupType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LookupTypeEntityToDTOConverter {

    LookupTypeEntityToDTOConverter INSTANCE = Mappers.getMapper(LookupTypeEntityToDTOConverter.class);

    LookupTypeDTO convert(LookupType lookupType);

}
