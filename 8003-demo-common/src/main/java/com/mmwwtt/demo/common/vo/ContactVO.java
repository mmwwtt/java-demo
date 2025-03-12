package com.mmwwtt.demo.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactVO {
    /**
     * 联系类型 1手机，2微信，3QQ,4邮箱
     */
    private String type;
    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 预置list
     */
    public static List<ContactVO> getPresetList() {
        List<ContactVO> list = new ArrayList<>();
        list.add(ContactVO.builder().type("1").contactInfo("15988487599").build());
        list.add(ContactVO.builder().type("2").contactInfo("mmwwtt_mwtttt").build());
        list.add(ContactVO.builder().type("3").contactInfo("2310607366").build());
        list.add(ContactVO.builder().type("4").contactInfo("2310607366@qq.com").build());
        return list;
    }

    /**
     * 预置对象
     */
    public static ContactVO getPresetSingle1() {
        return ContactVO.builder().type("1").contactInfo("15988487599").build();
    }
}
