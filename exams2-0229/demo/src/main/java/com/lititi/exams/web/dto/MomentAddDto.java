package com.lititi.exams.web.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class MomentAddDto {
    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 动态内容
     */
    @Length(min = 1, max = 500, message = "内容不能超过500个字符")
    @NotEmpty(message = "发布的内容不能为空")
    private String content;
    /**
     * 位置
     */
    private String position;
    /**
     * 图片列表
     */
    @Size(max = 9, message = "图片不能超过9张")
    private List<String> imgPathList;
}
