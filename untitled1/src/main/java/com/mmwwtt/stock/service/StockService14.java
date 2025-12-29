package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockService14 implements StockService {

    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i <= list.size() - getDayNum(); i++) {
            List<StockDetail> curList = list.subList(i, i + 5);
            StockDetail stockDetail = curList.get(0);
            StockDetail stockDetail1 = curList.get(1);
            StockDetail stockDetail2 = curList.get(2);
            double fiveAverageDealQuentity = curList.stream().skip(1).limit(3)
                    .mapToDouble(StockDetail::getAllDealQuantity).average().getAsDouble();
            double fiveAverageDealQuentity1 = curList.stream().skip(2).limit(3)
                    .mapToDouble(StockDetail::getAllDealQuantity).average().getAsDouble();
            double fiveAverageDealQuentity2 = curList.stream().skip(3).limit(3)
                    .mapToDouble(StockDetail::getAllDealQuantity).average().getAsDouble();
            if (stockDetail.getAllDealQuantity() < fiveAverageDealQuentity * 1.1
                    || stockDetail1.getAllDealQuantity() < fiveAverageDealQuentity1 * 1.1
                    || stockDetail2.getAllDealQuantity() < fiveAverageDealQuentity2 * 1.1) {
                continue;
            }
            if (stockDetail.getPricePert() < 0) {
                continue;
            }
            res.add(new OneRes(list.get(i - 1)));
        }
        return res;
    }

    public String getStrategy() {
        return this.getClass().getSimpleName();
    }

    public int getDayNum() {
        return 5;
    }
}
