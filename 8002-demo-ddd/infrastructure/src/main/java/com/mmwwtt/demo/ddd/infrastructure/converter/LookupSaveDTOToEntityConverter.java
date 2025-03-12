package com.mmwwtt.demo.ddd.infrastructure.converter;

import com.mmwwtt.demo.ddd.infrastructure.dto.lookup.LookupSaveDTO;
import com.mmwwtt.demo.ddd.domain.entity.Lookup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;


@Mapper
public interface LookupSaveDTOToEntityConverter{
    LookupSaveDTOToEntityConverter INSTANCE = Mappers.getMapper(LookupSaveDTOToEntityConverter.class);

    @Mappings({@Mapping(target = "deleteFlag", ignore = true)})
    Lookup convert(LookupSaveDTO lookupSaveDTO);

}
