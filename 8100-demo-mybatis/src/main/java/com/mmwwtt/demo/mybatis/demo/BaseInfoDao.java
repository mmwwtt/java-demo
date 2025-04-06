package com.mmwwtt.demo.mybatis.demo;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseInfoDao {
    List<BaseInfo> getByQuery(BaseInfo baseInfo);
    void add(BaseInfo baseInfo);
    void addAll(List<BaseInfo> list);

    void update(BaseInfo baseInfo);
    void updateAll(List<BaseInfo> list);
}
