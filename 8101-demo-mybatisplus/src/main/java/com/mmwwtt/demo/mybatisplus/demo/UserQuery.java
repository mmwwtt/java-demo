package com.mmwwtt.demo.mybatisplus.demo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

@Data
public class UserQuery extends Page {
    private Long userId;
    private String name;

    private List<Long> sexCodeList;
    private List<String> userIdList;

}
