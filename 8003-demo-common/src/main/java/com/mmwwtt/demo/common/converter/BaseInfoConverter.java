package com.mmwwtt.demo.common.converter;

import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(builder = @Builder(disableBuilder = true))
public interface BaseInfoConverter {
    BaseInfoConverter INSTANCE = Mappers.getMapper(BaseInfoConverter.class);

    BaseInfo converterToEntity(BaseInfoVO vo);

    List<BaseInfo> converterToEntity(List<BaseInfoVO> list);

    BaseInfoVO converterToVO(BaseInfo baseInfo);

    List<BaseInfoVO> converterToVO(List<BaseInfo> list);
}
