package com.mmwwtt.demo.mybatis.demo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FamilyDAO {
    List<Family> queryFamilyByUserId(@Param("userId") Long userId);
}
