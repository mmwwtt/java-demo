package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

@Data
public class BaseInfoQuery extends Page {
    private String baseInfoId;
    private String name;

    private List<String> sexList;
    private List<String> baseInfoIdList;

}
