package com.mmwwtt.demo.common.dto;

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
public class BaseInfoDTO {
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
    private List<ContactDTO> contactList;
    private AddressDTO address;

    /**
     * 预置list
     */
    public static List<BaseInfoDTO> getPresetList() {
        List<BaseInfoDTO> list = new ArrayList<>();
        list.add(BaseInfoDTO.builder().name("小白").age(10).sexCode("0").contactList(ContactDTO.getPresetList())
                .address(AddressDTO.getPresetSingle1()).build());
        list.add(BaseInfoDTO.builder().name("小红").age(11).sexCode("0").contactList(ContactDTO.getPresetList())
                .address(AddressDTO.getPresetSingle1()).build());
        list.add(BaseInfoDTO.builder().name("小兰").age(12).sexCode("0").contactList(ContactDTO.getPresetList())
                .address(AddressDTO.getPresetSingle1()).build());
        list.add(BaseInfoDTO.builder().name("小明").age(13).sexCode("0").contactList(ContactDTO.getPresetList())
                .address(AddressDTO.getPresetSingle1()).build());
        return list;
    }

    /**
     * 预置对象
     */
    public static BaseInfoDTO getPresetSingle1() {
        return BaseInfoDTO.builder().name("小白").age(10).sexCode("0").contactList(ContactDTO.getPresetList())
                .address(AddressDTO.getPresetSingle1()).build();
    }

    public static BaseInfoDTO getPresetSingle2() {
        return BaseInfoDTO.builder().name("小红").age(11).sexCode("0").contactList(ContactDTO.getPresetList())
                .address(AddressDTO.getPresetSingle1()).build();
    }

    public static BaseInfoDTO getPresetSingle3() {
        return BaseInfoDTO.builder().name("小兰").age(12).sexCode("0").contactList(ContactDTO.getPresetList())
                .address(AddressDTO.getPresetSingle1()).build();
    }
}
