package com.mmwwtt.demo.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseInfo extends BaseModel{
    private Long baseInfoId;
    private String name;
    private String sexCode;
    private String sexName;
    private Integer age;
    private Date birthDate;
    private String employeeNumber;
    private String firstEducationCode;
    private String firstEducationName;
    private Integer salary;
    private List<Contact> contactList;
    private String mainContact;
    private String phoneNumber;
    private String address;

    /**
     * 预置list
     */
    public static List<BaseInfo> getPresetList() {
        List<BaseInfo> list = new ArrayList<>();
        list.add(BaseInfo.builder().name("小白").age(10).sexCode("0").contactList(Contact.getPresetList()).build());
        list.add(BaseInfo.builder().name("小红").age(11).sexCode("0").contactList(Contact.getPresetList()).build());
        list.add(BaseInfo.builder().name("小兰").age(12).sexCode("0").contactList(Contact.getPresetList()).build());
        list.add(BaseInfo.builder().name("小明").age(13).sexCode("0").contactList(Contact.getPresetList()).build());
        return list;
    }

    /**
     * 预置对象
     */
    public static BaseInfo getPresetSingle1() {
        return BaseInfo.builder().name("小白").age(10).sexCode("0").contactList(Contact.getPresetList()).build();
    }

    public static BaseInfo getPresetSingle2() {
        return BaseInfo.builder().name("小红").age(11).sexCode("0").contactList(Contact.getPresetList()).build();
    }

    public static BaseInfo getPresetSingle3() {
        return BaseInfo.builder().name("小兰").age(12).sexCode("0").contactList(Contact.getPresetList()).build();
    }
}
