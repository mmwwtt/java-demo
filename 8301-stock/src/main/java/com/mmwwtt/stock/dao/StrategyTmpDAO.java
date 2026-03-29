package com.mmwwtt.stock.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mmwwtt.stock.entity.strategy.StrategyTmp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StrategyTmpDAO extends BaseMapper<StrategyTmp> {

    /**
     * 查询id列表
     */
    @Select("SELECT strategy_id,strategy_code,date_cnt,detail_cnt,pert,level\n" +
            "FROM (SELECT strategy_id, strategy_code,date_cnt,detail_cnt,pert,level,\n" +
            "    ROW_NUMBER() OVER (PARTITION BY level ORDER BY pert DESC) AS rn FROM strategy_tmp_t where level >3 ) sub\n" +
            "WHERE rn <= 2000;")
    List<StrategyTmp> getAfterTmp();
}
