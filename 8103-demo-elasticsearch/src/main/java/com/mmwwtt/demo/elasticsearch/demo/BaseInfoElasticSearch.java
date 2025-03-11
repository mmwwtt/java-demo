package com.mmwwtt.demo.elasticsearch.demo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "baseinfoelasticsearch",createIndex = true)
public class BaseInfoElasticSearch {
    @Id
    private String baseInfoId;
    private String name;
    private String sex;
    private Double height;
}
