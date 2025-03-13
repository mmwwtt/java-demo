package com.mmwwtt.demo.common.vo;

import com.mmwwtt.demo.common.converter.FamilyConverter;
import com.mmwwtt.demo.common.entity.Family;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FamilyVO {
    private Long familyId;
    private String name;
    private String sexCode;
    private LocalDate birthDate;

    /**
     * 预置list
     */
    public static List<FamilyVO> getPresetList() {
        return FamilyConverter.INSTANCE.converterToVO(Family.getPresetList());
    }

    /**
     * 预置对象
     */
    public static FamilyVO getInstance() {
        return FamilyConverter.INSTANCE.converterToVO(Family.getInstance());
    }
}
