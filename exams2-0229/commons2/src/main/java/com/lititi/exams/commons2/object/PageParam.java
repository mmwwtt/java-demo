package com.lititi.exams.commons2.object;

import lombok.Data;

@Data
public class PageParam {

    /**
     * 页码
     */
    private Integer pageNo;

    /**
     * 一页大小
     */
    private Integer pageSize;

    /**
     * 页的开始索引
     */
    private int pageIndex;

    /**
     * 错误信息
     */
    private String errorMsg;
}
