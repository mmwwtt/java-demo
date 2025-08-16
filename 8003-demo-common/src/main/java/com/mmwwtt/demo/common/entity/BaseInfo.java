package com.mmwwtt.demo.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseInfo extends BaseModel {
    private Long baseInfoId;
    private String name;
    private String sexCode;
    private Double height;
    private LocalDate birthDate;
    private Contact contact;
    private List<Family> familyList;
    private Map<String, Family> map;


    /**
     * 预置list
     */
    public static List<BaseInfo> getPresetList() {
        List<BaseInfo> list = new ArrayList<>();
        list.add(BaseInfo.builder().baseInfoId(1L).name("小白").height(171.5).sexCode("0").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build());
        list.add(BaseInfo.builder().baseInfoId(2L).name("小红").height(165.5).sexCode("1").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build());
        list.add(BaseInfo.builder().baseInfoId(3L).name("小兰").height(181.5).sexCode("0").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build());
        list.add(BaseInfo.builder().baseInfoId(4L).name("小明").height(191.5).sexCode("1").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build());
        return list;
    }

    /**
     * 预置对象
     */
    public static BaseInfo getInstance() {
        return BaseInfo.builder().baseInfoId(1L).name("小白").height(171.5).sexCode("0").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build();
    }

}
