package com.mmwwtt.demo.ee.mapStruct;

import com.mmwwtt.demo.common.entity.BaseInfo;
import org.mapstruct.Mapper;
import org.mapstruct.control.DeepClone;

@Mapper(mappingControl = DeepClone.class )
public interface DeepConverter {
    BaseInfo convertToBaseInfo(BaseInfo baseInfo);
}
