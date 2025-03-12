package com.mmwwtt.demo.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 联系方式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Contact extends BaseModel {
    private Long contactId;
    private String email;
    private String phoneNumber;
    private String address;

    public static Contact getInstance() {
        return Contact.builder().contactId(1L).email("qqwwee.qq.com")
                .phoneNumber("12344563456").address("新街街道盛东村").build();
    }
}
