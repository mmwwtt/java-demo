package com.mmwwtt.stock.service.b_下引线;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockServiceB8 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i <= list.size() - getDayNum(); i++) {
            List<StockDetail> curList = list.subList(i, i + 5);
            StockDetail stockDetail = curList.get(0);
            //上影线长度占实体1/3以上  表示抛压大
            if (stockDetail.getLowerShadowLength() / stockDetail.getAllLength() < 0.8) {
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
