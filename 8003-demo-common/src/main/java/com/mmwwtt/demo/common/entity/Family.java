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
public class Family extends BaseModel{
    private Long familyId;
    private String name;
    private String sexCode;
    private LocalDate birthday;

    /**
     * 预置list
     */
    public static List<Family> getPresetList() {
        List<Family> list = new ArrayList<>();
        list.add(Family.builder().familyId(1L).name("欢欢").sexCode("0").build());
        list.add(Family.builder().familyId(2L).name("小张").sexCode("0").build());
        list.add(Family.builder().familyId(3L).name("小名").sexCode("0").build());
        list.add(Family.builder().familyId(4L).name("小谢").sexCode("0").build());
        return list;
    }

    /**
     * 预置对象
     */
    public static Family getInstance() {
        return Family.builder().familyId(1L).name("欢欢").sexCode("0").build();
    }
}
