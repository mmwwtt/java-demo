package com.mmwwtt.stock.convert;

import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StockDetailVO;
import com.mmwwtt.stock.entity.StockVO;
import org.mapstruct.*;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import java.math.RoundingMode;
import java.util.List;

@Mapper(mappingControl = DeepClone.class)
public interface StockConverter {
    StockConverter INSTANCE = Mappers.getMapper(StockConverter.class);
    @Mappings({
            @Mapping(target = "stockCode", source = "stockDetailVO.stockCode"),
            @Mapping(target = "dealDate", source = "stockDetailVO.t"),
            @Mapping(target = "startPrice", source = "stockDetailVO.o"),
            @Mapping(target = "highPrice", source = "stockDetailVO.h"),
            @Mapping(target = "lowPrice", source = "stockDetailVO.l"),
            @Mapping(target = "endPrice", source = "stockDetailVO.c"),
            @Mapping(target = "dealQuantity", source = "stockDetailVO.v"),
            @Mapping(target = "dealPrice", source = "stockDetailVO.a"),
            @Mapping(target = "lastPrice", source = "stockDetailVO.pc")
    })
    StockDetail convertToStockDetail(StockDetailVO stockDetailVO);

    List<StockDetail> convertToStockDetail(List<StockDetailVO> stockDetailVO);

    @Mappings({
            @Mapping(target = "code", source = "stock.dm"),
            @Mapping(target = "name", source = "stock.mc"),
    })
    Stock convertToStock(StockVO stock);

    List<Stock> convertToStock(List<StockVO> stockList);

    @AfterMapping
    default void convertBigDecimal(@MappingTarget StockDetail stockDetail) {
        stockDetail.setPricePert(stockDetail.getEndPrice().subtract(stockDetail.getLastPrice()).divide(stockDetail.getLastPrice(),4, RoundingMode.HALF_UP));
        stockDetail.setStartPrice(stockDetail.getStartPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setHighPrice(stockDetail.getHighPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setLowPrice(stockDetail.getLowPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setEndPrice(stockDetail.getEndPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setDealPrice(stockDetail.getDealQuantity().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setDealPrice(stockDetail.getDealPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setLastPrice(stockDetail.getLastPrice().setScale(4, RoundingMode.HALF_UP));
    }
}
