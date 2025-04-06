package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseInfoDao extends BaseMapper<BaseInfo> {
    List<BaseInfo> query(@Param("query") BaseInfoQuery query);
    Page<BaseInfo> queryByPage(@Param("query") BaseInfoQuery query);
}
