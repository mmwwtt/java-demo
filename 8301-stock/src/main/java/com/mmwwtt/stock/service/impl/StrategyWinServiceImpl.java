package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StrategyWinDAO;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.enums.StrategyEnum;
import com.mmwwtt.stock.service.StrategyWinService;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mmwwtt.stock.common.CommonUtils.add;
import static com.mmwwtt.stock.common.CommonUtils.divide;

@Service
public class StrategyWinServiceImpl extends ServiceImpl<StrategyWinDAO, StrategyWin> implements StrategyWinService {

    @Resource
    private StrategyWinDAO strategyWinDAO;

    @Override
    public void saveByDetails(List<StockDetail> allAfterList, String strategyCode, LocalDateTime now) {
        if (CollectionUtils.isEmpty(allAfterList)) {
            return;
        }

        BigDecimal winPriceRateSum = BigDecimal.ZERO;
        int winCnt = 0;
        BigDecimal onePriceRateSum = BigDecimal.ZERO;
        int oneCnt = 0;
        BigDecimal twoPriceRateSum = BigDecimal.ZERO;
        int twoCnt = 0;
        BigDecimal threePriceRateSum = BigDecimal.ZERO;
        int threeCnt = 0;
        BigDecimal fourPriceRateSum = BigDecimal.ZERO;
        int fourCnt = 0;
        BigDecimal fivePriceRateSum = BigDecimal.ZERO;
        BigDecimal fiveMaxPriceRateSum = BigDecimal.ZERO;
        int fiveCnt = 0;
        BigDecimal tenPriceRateSum = BigDecimal.ZERO;
        BigDecimal tenMaxPriceRateSum = BigDecimal.ZERO;
        int tenCnt = 0;

        for (StockDetail stockDetail : allAfterList) {
            if (Objects.isNull(stockDetail)) {
                continue;
            }
            if (Objects.nonNull(stockDetail.getNext1())) {
                onePriceRateSum = add(onePriceRateSum, stockDetail.getNext1().getPricePert());
                oneCnt++;
                if (stockDetail.getNext1().getIsUp()) {
                    winPriceRateSum = add(winPriceRateSum, stockDetail.getNext1().getPricePert());
                    winCnt++;
                }
            }
            if (Objects.nonNull(stockDetail.getNext2())) {
                twoPriceRateSum = add(twoPriceRateSum, stockDetail.getNext2().getPricePert());
                twoCnt++;
            }

            if (Objects.nonNull(stockDetail.getNext3())) {
                threePriceRateSum = add(threePriceRateSum, stockDetail.getNext3().getPricePert());
                threeCnt++;
            }


            if (Objects.nonNull(stockDetail.getNext4())) {
                fourPriceRateSum = add(fourPriceRateSum, stockDetail.getNext4().getPricePert());
                fourCnt++;
            }


            if (Objects.nonNull(stockDetail.getNext5())) {
                fivePriceRateSum = add(fivePriceRateSum, stockDetail.getNext5().getPricePert());
                fiveMaxPriceRateSum = add(fiveMaxPriceRateSum, stockDetail.getNext5().getNext5MaxPricePert());
                fiveCnt++;
            }


            if (Objects.nonNull(stockDetail.getNext10())) {
                tenPriceRateSum = add(tenPriceRateSum, stockDetail.getNext10().getPricePert());
                tenMaxPriceRateSum = add(tenMaxPriceRateSum, stockDetail.getNext10().getNext10MaxPricePert());
                tenCnt++;
            }
        }
        String  strategyName = Arrays.stream(strategyCode.split(" ")).map(StrategyEnum.codeToNameMap::get).collect(Collectors.joining(" "));

        StrategyWin strategyWin = new StrategyWin();
        strategyWin.setStrategyCode(strategyCode);
        strategyWin.setStrategyName(strategyName);
        strategyWin.setWinRate(divide(winCnt, oneCnt));
        strategyWin.setWinPercRate(divide(winPriceRateSum, winCnt));
        strategyWin.setOnePercRate(divide(onePriceRateSum, oneCnt));
        strategyWin.setTwoPercRate(divide(twoPriceRateSum, twoCnt));
        strategyWin.setThreePercRate(divide(threePriceRateSum, threeCnt));
        strategyWin.setFourPercRate(divide(fourPriceRateSum, fourCnt));
        strategyWin.setFivePercRate(divide(fivePriceRateSum, fiveCnt));
        strategyWin.setTenPercRate(divide(tenPriceRateSum, tenCnt));
        strategyWin.setTenMaxPercRate(divide(tenMaxPriceRateSum, tenCnt));
        strategyWin.setFiveMaxPercRate(divide(fiveMaxPriceRateSum, fiveCnt));
        strategyWin.setCreateDate(now);
        strategyWin.setCnt(allAfterList.size());
        save(strategyWin);
    }

    @Override
    public List<StrategyWin> getStrategyWin(StrategyWin strategyWin) {
        QueryWrapper<StrategyWin> wapper = new QueryWrapper<>();
        if(Objects.nonNull(strategyWin.getLevel())) {
            wapper.eq("level", strategyWin.getLevel());
        }
        if(Objects.nonNull(strategyWin.getWinRate())) {
            wapper.ge("win_rate", strategyWin.getWinRate());
        }
        return list(wapper);
    }
}
