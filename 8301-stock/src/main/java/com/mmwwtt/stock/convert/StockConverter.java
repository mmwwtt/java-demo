package com.mmwwtt.stock.convert;

import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.vo.StockVO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(mappingControl = DeepClone.class, builder = @Builder(disableBuilder = true))
public interface StockConverter {
    StockConverter INSTANCE = Mappers.getMapper(StockConverter.class);

    @Mappings({
            @Mapping(target = "t1", ignore = true),
            @Mapping(target = "t2", ignore = true),
            @Mapping(target = "t3", ignore = true),
            @Mapping(target = "t4", ignore = true),
            @Mapping(target = "t5", ignore = true),
            @Mapping(target = "next1", ignore = true),
            @Mapping(target = "next2", ignore = true),
            @Mapping(target = "next3", ignore = true),
            @Mapping(target = "next4", ignore = true),
            @Mapping(target = "next5", ignore = true),
            @Mapping(target = "next10", ignore = true)
    })
    Detail convertToDetail(Detail detail);


    List<Stock> convertToStock(List<StockVO> stockList);
}
