package com.mmwwtt.demo.ee.mapStruct;

import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.ee.mapStruct.tmpEntity.A;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

@Mapper(mappingControl = DeepClone.class)
public interface DeepConverter {
    DeepConverter INSTANCE = Mappers.getMapper(DeepConverter.class);

    //会进行深拷贝
    BaseInfo convertToBaseInfo(BaseInfo baseInfo);
    List<BaseInfo> convertToBaseInfo(List<BaseInfo> list);

    //对于map中的泛型 如Map<String, List<Integer>>   Map<String,List<String>> 都会识别成Map<String, List>  所以需要自己加方法
    A convert(A a);
    Map<String, Map<String,BaseInfo>> convert(Map<String,Map<String,BaseInfo>>  map);
    List<Map<String,BaseInfo>> convert(List<Map<String,BaseInfo>> list);
    Map<String,BaseInfo> convert1(Map<String,BaseInfo> map);


    /**
     * 将指定值赋给新对象
     * @param baseInfo
     * @param name
     * @return
     */
    @Mapping(target = "name", expression = "java(name)")
    BaseInfo convertToReactionGroup(BaseInfo baseInfo, String name);
}
