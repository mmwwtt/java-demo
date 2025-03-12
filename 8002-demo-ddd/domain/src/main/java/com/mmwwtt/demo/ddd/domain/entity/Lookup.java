package com.mmwwtt.demo.ddd.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@TableName("lookup_t")
public class Lookup extends BaseEntity{
    @TableId(type = IdType.AUTO)
    Long lookupId;


    @TableField(value = "code")
    String code;

    @TableField(value = "name_cn")
    String nameCN;

    @TableField(value = "name_en")
    String nameEN;

    @TableField(value = "note")
    String note;

    List<Lookup> childLookupList;
}
