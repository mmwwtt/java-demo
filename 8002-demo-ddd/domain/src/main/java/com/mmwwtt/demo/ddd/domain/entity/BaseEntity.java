package com.mmwwtt.demo.ddd.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class BaseEntity {
    @TableField(value = "create_date")
    private Date createDate;

    @TableField(value = "create_by_name")
    private String createByName;

    @TableField(value = "create_by_id")
    private Long createById;

    @TableField(value = "last_update_date")
    private Date lastUpdateDate;

    @TableField(value = "last_update_by_name")
    private String lastUpdateByName;

    @TableField(value = "last_update_by_id")
    private Long lastUpdateById;

    @TableField(value = "delete_flag")
    private String deleteFlag;
}
