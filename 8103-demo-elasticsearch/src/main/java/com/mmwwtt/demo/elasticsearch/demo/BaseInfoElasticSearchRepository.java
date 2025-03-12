package com.mmwwtt.demo.elasticsearch.demo;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * 方法明明约定，自定生成对应查询
 */
public interface BaseInfoElasticSearchRepository extends ElasticsearchRepository<BaseInfoElasticSearch, String> {
    //单字段查询
    List<BaseInfoElasticSearch> queryByName(String name);

    //多字段查询
    List<BaseInfoElasticSearch> queryByNameAndSexCode(String name, String sexCode);

    //Containing模糊查询
    List<BaseInfoElasticSearch> queryByNameContaining(String keyword);

    //范围查询
    List<BaseInfoElasticSearch> queryByHeightBetween(Double low, Double high);

    //@Query 直接使用ES查询语句 ?0表示第0号入参的占位符
    @Query("{\"bool\":{\"must\":[{\"match\":{\"name\":\"?0\"}}]}}")
    List<BaseInfoElasticSearch> queryByNameEsSql(String name);

}

