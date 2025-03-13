package com.mmwwtt.demo.swagger.demo;

import com.mmwwtt.demo.common.entity.Contact;
import com.mmwwtt.demo.common.entity.Family;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "基本信息信息")
@Data
public class BaseInfoSwagger {
    @Schema(description = "基本信息ID", example = "1")
    private Long baseInfoId;
    @Schema(description = "星梦", example = "欢欢")
    private String name;
    @Schema(description = "性别编码", example = "1")
    private String sexCode;
    @Schema(description = "身高", example = "171.77")
    private Double height;
    @Schema(description = "出生年月日", example = "2000-04-07")
    private LocalDate birthDate;
    @Schema(description = "联系方式", example = "")
    private Contact contact;
    @Schema(description = "家庭成员列表", example = "")
    private List<Family> familyList;
}
