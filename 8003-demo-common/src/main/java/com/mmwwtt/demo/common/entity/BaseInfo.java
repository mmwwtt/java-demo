package com.mmwwtt.demo.common.entity;

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
public class BaseInfo extends BaseModel {
    private Long baseInfoId;
    private String name;
    private String sexCode;
    private LocalDate birthDate;
    private Contact contact;
    private List<Family> familyList;


    /**
     * 预置list
     */
    public static List<BaseInfo> getPresetList() {
        List<BaseInfo> list = new ArrayList<>();
        list.add(BaseInfo.builder().name("小白").sexCode("0").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build());
        list.add(BaseInfo.builder().name("小红").sexCode("0").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build());
        list.add(BaseInfo.builder().name("小兰").sexCode("0").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build());
        list.add(BaseInfo.builder().name("小明").sexCode("0").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build());
        return list;
    }

    /**
     * 预置对象
     */
    public static BaseInfo getInstance() {
        return BaseInfo.builder().baseInfoId(1L).name("小白").sexCode("0").contact(Contact.getInstance())
                .familyList(Family.getPresetList()).build();
    }

}
