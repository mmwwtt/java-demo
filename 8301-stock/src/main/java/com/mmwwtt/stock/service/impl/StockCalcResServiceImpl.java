package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.StockCalcResDao;
import com.mmwwtt.stock.entity.StockCalcRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockCalcResService;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.mmwwtt.stock.common.CommonUtils.add;
import static com.mmwwtt.stock.common.CommonUtils.divide;

@Service
public class StockCalcResServiceImpl extends ServiceImpl<StockCalcResDao, StockCalcRes> implements StockCalcResService {

    @Resource
    private StockCalcResDao stockCalcResDao;

    @Override
    public void saveCalcRes(List<StockDetail> allAfterList, String strategyDesc, LocalDateTime dataTime, String type) {
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

        StockCalcRes calcRes = new StockCalcRes();
        calcRes.setStrategyDesc(strategyDesc);
        calcRes.setWinRate(divide(winCnt, oneCnt));
        calcRes.setWinPercRate(divide(winPriceRateSum, winCnt));
        calcRes.setPercRate(divide(onePriceRateSum, oneCnt));
        calcRes.setTwoPercRate(divide(twoPriceRateSum, twoCnt));
        calcRes.setThreePercRate(divide(threePriceRateSum, threeCnt));
        calcRes.setFourPercRate(divide(fourPriceRateSum, fourCnt));
        calcRes.setFivePercRate(divide(fivePriceRateSum, fiveCnt));
        calcRes.setTenPercRate(divide(tenPriceRateSum, tenCnt));
        calcRes.setTenMaxPercRate(divide(tenMaxPriceRateSum, tenCnt));
        calcRes.setFiveMaxPercRate(divide(fiveMaxPriceRateSum, fiveCnt));
        calcRes.setCreateDate(dataTime);
        calcRes.setAllCnt(allAfterList.size());
        calcRes.setType(type);

        stockCalcResDao.insert(calcRes);
    }
}
