package com.mmwwtt.stock.convert;

import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.entity.Stock;
import com.mmwwtt.stock.entity.strategy.Strategy;
import com.mmwwtt.stock.entity.strategy.StrategyL1;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.vo.DetailOnTimeVO;
import com.mmwwtt.stock.vo.DetailVO;
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



    List<Detail> convertToDetail(List<DetailVO> detailVO);

    @Mappings({
            @Mapping(target = "code", source = "stock.dm"),
            @Mapping(target = "name", source = "stock.mc"),
    })
    Stock convertToStock(StockVO stock);

    List<Stock> convertToStock(List<StockVO> stockList);



    @Mappings({
            @Mapping(target = "stockCode", source = "detailVO.stockCode"),
            @Mapping(target = "dealDate", source = "detailVO.t"),
            @Mapping(target = "startPrice", source = "detailVO.o"),
            @Mapping(target = "highPrice", source = "detailVO.h"),
            @Mapping(target = "lowPrice", source = "detailVO.l"),
            @Mapping(target = "endPrice", source = "detailVO.c"),
            @Mapping(target = "dealQuantity", source = "detailVO.v"),
            @Mapping(target = "dealPrice", source = "detailVO.a"),
            @Mapping(target = "lastPrice", source = "detailVO.pc")
    })
    Detail convertToDetail(DetailVO detailVO);


    @Mappings({
            @Mapping(target = "stockCode", source = "detailVO.stockCode"),
            @Mapping(target = "dealDate", source = "detailVO.t"),
            @Mapping(target = "startPrice", source = "detailVO.o"),
            @Mapping(target = "highPrice", source = "detailVO.h"),
            @Mapping(target = "lowPrice", source = "detailVO.l"),
            @Mapping(target = "endPrice", source = "detailVO.p"),
            @Mapping(target = "dealQuantity", source = "detailVO.v"),
            @Mapping(target = "dealPrice", source = "detailVO.cje"),
            @Mapping(target = "lastPrice", source = "detailVO.yc")
    })
    Detail convertToDetail(DetailOnTimeVO detailVO);

    @AfterMapping
    default void convertBigDecimal(@MappingTarget Detail detail) {
        detail.setPricePert(divide(subtract(detail.getEndPrice(), detail.getLastPrice()), detail.getLastPrice()));
        detail.setDealDate(detail.getDealDate().replaceAll("-", ""));
    }

    Map<String, Set<Integer>> convertToMap(Map<String, Set<Integer>> map);
    Set<Integer> convertToSet(Set<Integer> set);

    StrategyEnum convertTo(StrategyEnum strategyEnum);

    StrategyTmp convertTo(StrategyL1 strategyL1);
    Strategy convertTo(StrategyTmp strategyTmp);
}
