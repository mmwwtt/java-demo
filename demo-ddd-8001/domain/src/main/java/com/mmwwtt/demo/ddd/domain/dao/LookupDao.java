package com.mmwwtt.demo.ddd.domain.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmwwtt.demo.ddd.domain.entity.Lookup;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface LookupDao extends BaseMapper<Lookup> {
    List<Lookup> getByQuery(Lookup lookup);
}
