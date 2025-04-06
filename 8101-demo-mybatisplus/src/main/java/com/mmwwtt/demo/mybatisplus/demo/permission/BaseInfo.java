package com.mmwwtt.demo.mybatisplus.demo.permission;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("base_info_t")
public class BaseInfo {

    @TableId(type = IdType.AUTO)
    private Long baseInfoId;


    private String name;


    private String sexCode;

}
