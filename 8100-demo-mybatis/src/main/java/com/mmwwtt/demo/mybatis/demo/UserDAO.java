package com.mmwwtt.demo.mybatis.demo;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDAO {
    //    一对多-子查询(嵌套查询)
    //    先查主表，进行分页，再对每条记录查询子表数据
    List<User> query(@Param("query") User query);

    // 一对多-关联查询(当子表数据每个字段都为null时才不会映射到list中，
    // 所以collection中的关联字段要用子表的，主表的字段必存在,会导致存在其他字段都是null的数据)
    // 分页查询的时候是根据关联查询后(一对多)的记录来分页，之后再存放到list中,导致数据分页不正确
    List<User> query1(@Param("query") User query);

    //方式3：推荐将主表查出来，再更具主表的关联键去查询子表，在java中进行分组set到数据中

    void save(@Param("user") User user);
    void saveAll(@Param("list") List<User> list);

    void update(@Param("user") User user);
    void updateAll(@Param("list") List<User> list);

}
