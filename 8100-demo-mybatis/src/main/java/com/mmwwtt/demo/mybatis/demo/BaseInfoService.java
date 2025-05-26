package com.mmwwtt.demo.mybatis.demo;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BaseInfoService {
    @Resource
    private BaseInfoDao baseInfoDao;


    public List<BaseInfo> getByQuery(BaseInfo baseInfo){return baseInfoDao.getByQuery(baseInfo);}


    public PageInfo<BaseInfo> getPageByQuery(BaseInfo baseInfo,int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BaseInfo> list = baseInfoDao.getByQuery(baseInfo);
        return new PageInfo<>(list);
    }

    public BaseInfo add(BaseInfo baseInfo) {
        baseInfoDao.add(baseInfo);
        return baseInfo;
    }
    public List<BaseInfo> addAll(List<BaseInfo> list) {
        baseInfoDao.addAll(list);
        return list;
    }

    public BaseInfo update(BaseInfo baseInfo) {
        baseInfoDao.update(baseInfo);
        return baseInfo;
    }
    public List<BaseInfo> updateAll(List<BaseInfo> list) {
        baseInfoDao.updateAll(list);
        return list;
    }

}
