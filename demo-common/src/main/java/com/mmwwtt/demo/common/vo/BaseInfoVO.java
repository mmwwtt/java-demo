package com.mmwwtt.demo.common.vo;

import com.mmwwtt.demo.common.entity.BaseModel;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseInfoVO extends BaseModel {

    private Long baseInfoId;
    private String name;

    private String sexCode;
    private String sexName;

    private Integer age;
    private Date birthDate;

    /**
     * 预置list
     */
    public static List<BaseInfoVO> getPresetList() {
        List<BaseInfoVO> list = new ArrayList<>();
        list.add(BaseInfoVO.builder().name("小白").age(10).sexCode("0").build());
        list.add(BaseInfoVO.builder().name("小红").age(11).sexCode("0").build());
        list.add(BaseInfoVO.builder().name("小兰").age(12).sexCode("0").build());
        list.add(BaseInfoVO.builder().name("小明").age(13).sexCode("0").build());
        return list;
    }

    /**
     * 预置对象
     */
    public static BaseInfoVO getPresetSingle1() {
        return BaseInfoVO.builder()
                .name("小白")
                .age(10)
                .sexCode("0")
                .build();
    }

    public static BaseInfoVO getPresetSingle2() {
        return BaseInfoVO.builder()
                .name("小红")
                .age(11)
                .sexCode("0")
                .build();
    }

    public static BaseInfoVO getPresetSingle3() {
        return BaseInfoVO.builder()
                .name("小兰")
                .age(12)
                .sexCode("0")
                .build();
    }

    public void format() {
        sexName = "0".equals(sexCode) ? "男" : "女";
    }

    public boolean valid(Integer age, String sexCode) {
        if (this.age != age || this.sexCode != sexCode) {
            return false;
        }
        return true;
    }
}
