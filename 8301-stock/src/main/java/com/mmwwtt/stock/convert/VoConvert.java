package com.mmwwtt.stock.convert;

import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.vo.StockDetailOnTimeVO;
import com.mmwwtt.stock.vo.StockDetailVO;
import com.mmwwtt.stock.vo.StockVO;
import org.mapstruct.*;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mmwwtt.stock.common.CommonUtils.divide;
import static com.mmwwtt.stock.common.CommonUtils.subtract;

@Mapper(mappingControl = DeepClone.class, builder = @Builder(disableBuilder = true))
public interface VoConvert {

    VoConvert INSTANCE = Mappers.getMapper(VoConvert.class);



    List<StockDetail> convertToStockDetail(List<StockDetailVO> stockDetailVO);

    @Mappings({
            @Mapping(target = "code", source = "stock.dm"),
            @Mapping(target = "name", source = "stock.mc"),
    })
    Stock convertToStock(StockVO stock);

    List<Stock> convertToStock(List<StockVO> stockList);



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


    @Mappings({
            @Mapping(target = "stockCode", source = "stockDetailVO.stockCode"),
            @Mapping(target = "dealDate", source = "stockDetailVO.t"),
            @Mapping(target = "startPrice", source = "stockDetailVO.o"),
            @Mapping(target = "highPrice", source = "stockDetailVO.h"),
            @Mapping(target = "lowPrice", source = "stockDetailVO.l"),
            @Mapping(target = "endPrice", source = "stockDetailVO.p"),
            @Mapping(target = "dealQuantity", source = "stockDetailVO.v"),
            @Mapping(target = "dealPrice", source = "stockDetailVO.cje"),
            @Mapping(target = "lastPrice", source = "stockDetailVO.yc")
    })
    StockDetail convertToStockDetail(StockDetailOnTimeVO stockDetailVO);

    @AfterMapping
    default void convertBigDecimal(@MappingTarget StockDetail stockDetail) {
        stockDetail.setPricePert(divide(subtract(stockDetail.getEndPrice(), stockDetail.getLastPrice()), stockDetail.getLastPrice()));
        stockDetail.setDealDate(stockDetail.getDealDate().replaceAll("-", ""));
        stockDetail.setStartPrice(stockDetail.getStartPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setHighPrice(stockDetail.getHighPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setLowPrice(stockDetail.getLowPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setEndPrice(stockDetail.getEndPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setDealQuantity(stockDetail.getDealQuantity().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setDealPrice(stockDetail.getDealPrice().setScale(4, RoundingMode.HALF_UP));
        stockDetail.setLastPrice(stockDetail.getLastPrice().setScale(4, RoundingMode.HALF_UP));
    }

    Map<String, Set<Integer>> convertToMap(Map<String, Set<Integer>> map);
    Set<Integer> convertToSet(Set<Integer> set);
}
