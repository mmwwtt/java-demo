package com.mmwwtt.demo.mybatisplus.demo.permission;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BaseInfoPermissDao extends BaseMapper<BaseInfo> {
    List<BaseInfo> getBaseInfoList();
}
