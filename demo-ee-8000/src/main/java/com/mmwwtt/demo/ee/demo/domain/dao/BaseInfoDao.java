package com.mmwwtt.demo.ee.demo.domain.dao;

import com.mmwwtt.demo.ee.demo.domain.entity.BaseInfo;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan
public class BaseInfoDao {

    public boolean addBaseInfo(BaseInfo baseInfo) {
        return true;
    }

    public void addBaseInfoNoReturn(BaseInfo baseInfo) {
    }
}
