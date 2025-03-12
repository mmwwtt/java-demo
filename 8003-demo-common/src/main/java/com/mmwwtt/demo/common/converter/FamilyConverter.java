package com.mmwwtt.demo.common.converter;

import com.mmwwtt.demo.common.entity.Family;
import com.mmwwtt.demo.common.vo.FamilyVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface FamilyConverter {
    FamilyConverter INSTANCE = Mappers.getMapper(FamilyConverter.class);

    Family converterToEntity(FamilyVO vo);

    FamilyVO converterToVO(Family entity);

    List<Family> converterToEntity(List<FamilyVO> list);

    List<FamilyVO> converterToVO(List<Family> list);
}
