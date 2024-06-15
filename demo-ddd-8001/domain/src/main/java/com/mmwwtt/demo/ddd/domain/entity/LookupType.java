package com.mmwwtt.demo.ddd.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("lookup_type_t")
public class LookupType extends BaseEntity{

    @TableId(type = IdType.AUTO)
    private Long lookupTypeId;

    @TableField("type")
    private String type;

    @TableField("name_cn")
    private String nameCN;

    @TableField("name_en")
    private String nameEN;

    @TableField("note")
    private String note;
}
