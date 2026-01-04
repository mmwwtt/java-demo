package com.mmwwtt.stock.service.complex;

import com.mmwwtt.stock.entity.OneRes;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.service.StockService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 啥也不做
 */
@Service
public class StockService11 implements StockService {


    public List<OneRes> run(List<StockDetail> list) {
        List<OneRes> res = new ArrayList<>();
        for (int i = 1; i <= list.size() - getDayNum(); i++) {

        }
        return res;
    }

    public String getStrategy() {
        return String.format("%s    %s", this.getClass().getSimpleName(), "啥也不做");
    }

    public int getDayNum() {
        return 5;
    }
}
