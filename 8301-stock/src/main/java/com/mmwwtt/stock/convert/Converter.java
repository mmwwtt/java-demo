package com.mmwwtt.stock.convert;

import com.mmwwtt.stock.entity.StockDetail;
import org.mapstruct.Mapper;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

@Mapper(mappingControl = DeepClone.class)
public interface Converter {
    Converter INSTANCE = Mappers.getMapper(Converter.class);

    StockDetail convertToStockDetail(StockDetail stockDetailVO);

}
