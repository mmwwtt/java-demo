package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockService0 implements StockService {


    public List<OneRes> run(List<StockDetail> list){
        List<OneRes> res = new ArrayList<>();
        for(int i = 1; i <= list.size()-getDayNum();i++) {
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
