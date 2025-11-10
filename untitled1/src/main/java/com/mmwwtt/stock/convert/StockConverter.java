package com.mmwwtt.stock.convert;

import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockDetailVO;
import com.mmwwtt.stock.entity.StockVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(mappingControl = DeepClone.class)
public interface StockConverter {
    StockConverter INSTANCE = Mappers.getMapper(StockConverter.class);
    @Mappings({
            @Mapping(target = "stockCode", expression = "java(stockCode)"),
            @Mapping(target = "dealDate", source = "stockDetailVO.t"),
            @Mapping(target = "startPrice", source = "stockDetailVO.o"),
            @Mapping(target = "highPrice", source = "stockDetailVO.h"),
            @Mapping(target = "lowPrice", source = "stockDetailVO.l"),
            @Mapping(target = "endPrice", source = "stockDetailVO.c"),
            @Mapping(target = "allDealQuantity", source = "stockDetailVO.v"),
            @Mapping(target = "allDealPrice", source = "stockDetailVO.a"),
            @Mapping(target = "lastPrice", source = "stockDetailVO.pc"),
            @Mapping(target = "pricePert", expression = "java((stockDetailVO.getC()-stockDetailVO.getPc())/stockDetailVO.getPc())")
    })
    StockDetail convertToStockDetail(StockDetailVO stockDetailVO, String stockCode);

    @Mappings({
            @Mapping(target = "code", source = "stock.dm"),
            @Mapping(target = "name", source = "stock.mc"),
    })
    Stock convertToStock(StockVO stock);

    List<Stock> convertToStock(List<StockVO> stockList);
}
