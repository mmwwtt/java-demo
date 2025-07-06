package com.lititi.exams.web.entity;

import lombok.Data;

import java.util.Date;

/**
 * The type Base model.
 */
@Data
public class BaseModel {
    /**
     * 创建时间
     */
    private Date createDate;
    /**
     * 最后修改时间
     */
    private Date lastUpdateDate;
    /**
     * 软删除标识 删除(Y) 未删除(N)
     */
    private String deleteFlag;
}
