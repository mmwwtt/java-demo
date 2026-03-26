package com.mmwwtt.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmwwtt.stock.entity.Detail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DetailDAO extends BaseMapper<Detail> {

    @Update("ALTER TABLE detail_t AUTO_INCREMENT = 1")
    void resetAutoIncrement();
}
