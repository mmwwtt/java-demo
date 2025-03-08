package com.mmwwtt.demo.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressDTO {
    private String address;
    /**
     * 预置对象
     */
    public static AddressDTO getPresetSingle1() {
        return AddressDTO.builder().address("新街街道").build();
    }
}
