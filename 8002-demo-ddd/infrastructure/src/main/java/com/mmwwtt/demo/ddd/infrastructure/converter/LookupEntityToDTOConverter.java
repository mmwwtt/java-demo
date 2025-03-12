package com.mmwwtt.demo.ddd.infrastructure.converter;

import com.mmwwtt.demo.ddd.infrastructure.dto.lookup.LookupDTO;
import com.mmwwtt.demo.ddd.domain.entity.Lookup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LookupEntityToDTOConverter {
    LookupEntityToDTOConverter INSTENCE = Mappers.getMapper(LookupEntityToDTOConverter.class);

    @Mappings({@Mapping(target = "name", source = "nameCN")})
    LookupDTO convert(Lookup lookup);
}
