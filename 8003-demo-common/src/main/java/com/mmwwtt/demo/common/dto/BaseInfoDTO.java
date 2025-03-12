package com.mmwwtt.demo.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseInfoDTO {
    private Long baseInfoId;
    private String name;
    private String sexCode;
    private String sexName;
    private LocalDate birthDate;



    /**
     * 预置list
     */
    public static List<BaseInfoDTO> getPresetList() {
        List<BaseInfoDTO> list = new ArrayList<>();
        list.add(BaseInfoDTO.builder().name("小白").sexCode("0").build());
        list.add(BaseInfoDTO.builder().name("小红").sexCode("0").build());
        list.add(BaseInfoDTO.builder().name("小兰").sexCode("0").build());
        list.add(BaseInfoDTO.builder().name("小明").sexCode("0").build());
        return list;
    }

    /**
     * 预置对象
     */
    public static BaseInfoDTO getPresetSingle1() {
        return BaseInfoDTO.builder().name("小白").sexCode("0").build();
    }

    public static BaseInfoDTO getPresetSingle2() {
        return BaseInfoDTO.builder().name("小红").sexCode("0").build();
    }

    public static BaseInfoDTO getPresetSingle3() {
        return BaseInfoDTO.builder().name("小兰").sexCode("0").build();
    }
}
