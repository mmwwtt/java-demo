package com.lititi.exams.web.dao;

import com.lititi.exams.web.entity.TestDemo;
import com.lititi.exams.commons2.annotation.Master;
import com.lititi.exams.commons2.annotation.Slave;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 演示文件，请勿在此文件添加或者修改代码！！！
 * 演示文件，请勿在此文件添加或者修改代码！！！
 * 演示文件，请勿在此文件添加或者修改代码！！！
 */

/**
 * (TestDemo)表数据库访问层
 *
 * @author makejava
 * @since 2024-02-20 11:08:56
 */
public interface TestDemoMapper {

    /**
     * 通过编号查询单条数据
     *
     * @param number 编号
     * @return 实例对象
     */
     @Slave
    TestDemo selectByNumber(long number);
    
    /**
     * 通过编号查询ID
     *
     * @param number 编号
     * @return ID
     */
     @Slave
    Long selectIdByNumber(long number);

    /**
     * 新增数据
     *
     * @param content 实例对象
     * @return 影响行数
     */
     @Master
    int insert(TestDemo content);

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param entities List<content> 实例对象列表
     * @return 影响行数
     */
     @Master
    int insertBatch(@Param("entities") List<TestDemo> entities);

    /**
     * 批量新增或按主键更新数据（MyBatis原生foreach方法）
     *
     * @param entities List<content> 实例对象列表
     * @return 影响行数
     * @throws org.springframework.jdbc.BadSqlGrammarException 入参是空List的时候会抛SQL语句错误的异常，请自行校验入参
     */
     @Master
    int insertOrUpdateBatch(@Param("entities") List<TestDemo> entities);

    /**
     * 修改数据
     *
     * @param content 实例对象
     * @return 影响行数
     */
     @Master
    int update(TestDemo content);

}

