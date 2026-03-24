package com.mmwwtt.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mmwwtt.stock.dao.DetailDAO;
import com.mmwwtt.stock.entity.Detail;
import com.mmwwtt.stock.service.CommonDataService;
import com.mmwwtt.stock.service.interfaces.DetailService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
@Slf4j
public class DetailServiceImpl extends ServiceImpl<DetailDAO, Detail> implements DetailService {

    @Override
    public List<Detail> getBySql(String sql) {
        QueryWrapper<Detail> wrapper = new QueryWrapper<>();
        wrapper.apply(sql);
        List<Detail> details = list(wrapper);
        return genAllDetail(details);
    }


    public List<Detail> genAllDetail(List<Detail> details) {
        for (int i = 0; i < details.size(); i++) {
            Detail t0 = details.get(i);
            List<Pair<Integer, Consumer<Detail>>> pairList = new ArrayList<>();
            pairList.add(Pair.of(i - 1, t0::setNext1));
            pairList.add(Pair.of(i - 2, t0::setNext2));
            pairList.add(Pair.of(i - 3, t0::setNext3));
            pairList.add(Pair.of(i - 4, t0::setNext4));
            pairList.add(Pair.of(i - 5, t0::setNext5));
            pairList.add(Pair.of(i - 10, t0::setNext10));
            pairList.add(Pair.of(i - 20, t0::setNext20));
            pairList.add(Pair.of(i + 1, t0::setT1));
            pairList.add(Pair.of(i + 2, t0::setT2));
            pairList.add(Pair.of(i + 3, t0::setT3));
            pairList.add(Pair.of(i + 4, t0::setT4));
            pairList.add(Pair.of(i + 5, t0::setT5));
            pairList.add(Pair.of(i + 6, t0::setT6));
            pairList.add(Pair.of(i + 7, t0::setT7));
            pairList.add(Pair.of(i + 8, t0::setT8));
            pairList.add(Pair.of(i + 9, t0::setT9));
            pairList.add(Pair.of(i + 10, t0::setT10));
            for (Pair<Integer, Consumer<Detail>> pair : pairList) {
                Integer idx = pair.getLeft();
                if (0 <= idx && idx < details.size()) {
                    Detail tmp = details.get(idx);
                    pair.getRight().accept(tmp);
                }
            }
        }
        return details;
    }

    @Override
    public Map<String, Detail> getCodeToCurDetailMap(String curDate) {
        Map<String, Detail> codeToDetailMap = new ConcurrentHashMap<>();
        log.info("开始查询数据");
        for (List<String> part : CommonDataService.stockCodePartList) {
            for (String stockCode : part) {
                Detail detail = CommonDataService.codeToDetailMap.get(stockCode).stream()
                        .filter(item -> Objects.equals(curDate, item.getDealDate()))
                        .findFirst().orElse(null);
                if (Objects.nonNull(detail)) {
                    codeToDetailMap.put(stockCode, detail);
                }
            }
        }
        log.info("开始查询数据-结束");
        return codeToDetailMap;
    }
}
