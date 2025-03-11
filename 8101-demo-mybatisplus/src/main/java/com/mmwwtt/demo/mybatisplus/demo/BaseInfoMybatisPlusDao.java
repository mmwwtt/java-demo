package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseInfoMybatisPlusDao extends BaseMapper<BaseInfoMyBatisPlus> {
    List<BaseInfoMyBatisPlus> query(@Param("query") BaseInfoMybatisPlusQuery query);
    Page<BaseInfoMyBatisPlus> queryByPage(@Param("query") BaseInfoMybatisPlusQuery query);
}
