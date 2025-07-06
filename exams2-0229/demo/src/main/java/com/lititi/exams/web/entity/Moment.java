package com.lititi.exams.web.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.List;

/**
 * 朋友圈动态实体类
 */
@Data
public class Moment extends BaseModel {
    /**
     * 主键
     */
    @Id
    private Long momentId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 内容
     */
    private String content;
    /**
     * 位置
     */
    private String position;
    /**
     * 图片地址列表
     */
    private List<String> imgPathList;
}
