package com.mmwwtt.stock.convert;

import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.vo.StockDetailOnTimeVO;
import com.mmwwtt.stock.vo.StockDetailVO;
import com.mmwwtt.stock.vo.StockVO;
import org.mapstruct.*;
import org.mapstruct.control.DeepClone;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.mmwwtt.stock.common.CommonUtils.divide;
import static com.mmwwtt.stock.common.CommonUtils.subtract;

@Mapper(mappingControl = DeepClone.class, builder = @Builder(disableBuilder = true))
public interface VoConvert {

    VoConvert INSTANCE = Mappers.getMapper(VoConvert.class);



    List<Detail> convertToStockDetail(List<StockDetailVO> stockDetailVO);

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
    Detail convertToStockDetail(StockDetailVO stockDetailVO);


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
    Detail convertToStockDetail(StockDetailOnTimeVO stockDetailVO);

    @AfterMapping
    default void convertBigDecimal(@MappingTarget Detail detail) {
        detail.setPricePert(divide(subtract(detail.getEndPrice(), detail.getLastPrice()), detail.getLastPrice()));
        detail.setDealDate(detail.getDealDate().replaceAll("-", ""));
    }

    Map<String, Set<Integer>> convertToMap(Map<String, Set<Integer>> map);
    Set<Integer> convertToSet(Set<Integer> set);

    StrategyEnum convertTo(StrategyEnum strategyEnum);

    StrategyTmp convertTo(StrategyL1 strategyL1);
}
