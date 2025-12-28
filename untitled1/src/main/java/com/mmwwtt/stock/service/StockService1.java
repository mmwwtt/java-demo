package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockService1 implements StockService{


    public List<OneRes> run(List<StockDetail> list){
        List<OneRes> res = new ArrayList<>();
        for(int i = 1; i <= list.size()-getDayNum();i++) {
            List<StockDetail> curList = list.subList(i,i+5);
            StockDetail stockDetail = curList.get(0);
            StockDetail stockDetail1 = curList.get(1);
            StockDetail stockDetail2 = curList.get(2);
            //比前5天的平均量*1.2倍  表示放量
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
            //比前一天量*2.5倍  放巨量 很危险
            if (stockDetail.getAllDealQuantity() > fiveAverageDealQuentity * 2.5) {
                continue;
            }
            //当天涨幅＜1%  / >6% /  前一天是跌的  排除不活跃和太活跃的
            if (stockDetail.getPricePert() < 0.015 || stockDetail.getPricePert() > 0.06 || stockDetail1.getPricePert() < 0) {
                continue;
            }
            //上影线长度占实体1/3以上  表示抛压大
            if (stockDetail.getUpperShadowLength() / stockDetail.getAllLength() > 0.33
                    || stockDetail1.getUpperShadowLength() / stockDetail1.getAllLength() > 0.33) {
                continue;
            }
            res.add(new OneRes(list.get(i - 1)));
        }
        return res;
    }

    public String getStrategy() {
        //获得当前类的名字
        return this.getClass().getSimpleName();
    }

    public int getDayNum() {
        return 5;
    }
}
