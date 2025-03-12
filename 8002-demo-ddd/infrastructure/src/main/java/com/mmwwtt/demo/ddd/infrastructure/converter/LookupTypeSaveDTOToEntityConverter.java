package com.mmwwtt.demo.ddd.infrastructure.converter;

import com.mmwwtt.demo.ddd.infrastructure.dto.lookuptype.LookupTypeSaveDTO;
import com.mmwwtt.demo.ddd.domain.entity.LookupType;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LookupTypeSaveDTOToEntityConverter {

    LookupTypeSaveDTOToEntityConverter INSTANCE = Mappers.getMapper(LookupTypeSaveDTOToEntityConverter.class);

    LookupType convert(LookupTypeSaveDTO lookupTypeSaveDTO);
}
