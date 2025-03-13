package com.mmwwtt.demo.common.dto;

import com.mmwwtt.demo.common.converter.BaseInfoConverter;
import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.entity.Contact;
import com.mmwwtt.demo.common.entity.Family;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseInfoDTO {
    private Long baseInfoId;
    private String name;
    private String sexCode;
    private Double height;
    private LocalDate birthDate;
    private Contact contact;
    private List<Family> familyList;

    private LocalDateTime nowTime;


    public Long lastUpdatedBy;
    public LocalDateTime lastUpdatedDate;
    public Long createdBy;
    public LocalDateTime createdDate;
    public String deleteFlag;


    private String email;
    private String phoneNumber;
    /**
     * 预置list
     */
    public static List<BaseInfoDTO> getPresetList() {
        return BaseInfoConverter.INSTANCE.converterToDTO(BaseInfo.getPresetList());
    }

    /**
     * 预置对象
     */

    public static BaseInfoDTO getInstance() {
        return BaseInfoConverter.INSTANCE.converterToDTO(BaseInfo.getInstance());
    }

}
