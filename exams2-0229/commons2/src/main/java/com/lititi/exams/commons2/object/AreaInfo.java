package com.lititi.exams.commons2.object;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaInfo implements Serializable {
    private static final long serialVersionUID = 5386091858501648328L;
    /**
     * id
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 父id
     */
    private Integer pid;
}
