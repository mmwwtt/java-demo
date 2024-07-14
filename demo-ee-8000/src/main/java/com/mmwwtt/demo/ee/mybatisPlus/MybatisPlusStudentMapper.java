package com.mmwwtt.demo.ee.mybatisPlus;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

public interface MybatisPlusStudentMapper extends BaseMapper<MybatisPlusStudent> {
    List<MybatisPlusStudent> selectByQuery(MybatisPlusStudentQuery mybatisPlusStudentQuery);
    Page<MybatisPlusStudent> selectPageByQuery(MybatisPlusStudentQuery mybatisPlusStudentQuery);
}
