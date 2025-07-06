package com.lititi.exams.web.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * 动态类dto
 */
@Data
public class MomentDto {
    /**
     * 动态id
     */
    private Integer momentId;

    /**
     * 用户id
     */
    private Integer userId;
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
    /**
     * 用户头像地址
     */
    private String userPicturePath;
    /**
     * 用户昵称
     */
    private String userName;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDate;
}
