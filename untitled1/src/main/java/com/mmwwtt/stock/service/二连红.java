package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class 二连红 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i <= list.size() - getDayNum(); i++) {
            StockDetail t0 = list.get(i);
            StockDetail t1 = list.get(i + 1);
            if (t0.getIsUp() && t1.getIsUp()) {
                res.add(new OneRes(list.get(i - 1)));
            }
        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s", this.getClass().getSimpleName());
    }

    public int getDayNum() {
        return 5;
    }
}
