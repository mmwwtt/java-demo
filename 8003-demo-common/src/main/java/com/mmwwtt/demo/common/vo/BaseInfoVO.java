package com.mmwwtt.demo.common.vo;

import com.mmwwtt.demo.common.converter.BaseInfoConverter;
import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.entity.BaseModel;
import com.mmwwtt.demo.common.entity.Contact;
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
public class BaseInfoVO extends BaseModel {

    private Long baseInfoId;
    private String name;
    private String sexCode;
    private String sexName;
    private LocalDate birthDate;
    private Contact contact;

    private List<FamilyVO> familyList;

    /**
     * 预置list
     */
    public static List<BaseInfoVO> getPresetList() {
        return BaseInfoConverter.INSTANCE.converterToVO(BaseInfo.getPresetList());
    }


    /**
     * 预置对象
     */

    public static BaseInfoVO getInstance() {
        return BaseInfoConverter.INSTANCE.converterToVO(BaseInfo.getInstance());
    }

    public void format() {
        sexName = "0".equals(sexCode) ? "男" : "女";
    }

    public boolean valid(String sexCode) {
        if (this.sexCode != sexCode) {
            return false;
        }
        return true;
    }
}
