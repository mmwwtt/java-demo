package com.mmwwtt.demo.ee.demo.domain.service;

import com.mmwwtt.demo.ee.demo.domain.dao.BaseInfoDao;
import com.mmwwtt.demo.ee.demo.domain.entity.BaseInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BaseInfoService {

    @Autowired
    private BaseInfoDao baseInfoDao;

    public boolean addBaseInfo(BaseInfo baseInfo) {
        return baseInfoDao.addBaseInfo(baseInfo);
    }

    public void addBaseInfoNoReturn(BaseInfo baseInfo) {
        baseInfoDao.addBaseInfoNoReturn(baseInfo);
    }
}
