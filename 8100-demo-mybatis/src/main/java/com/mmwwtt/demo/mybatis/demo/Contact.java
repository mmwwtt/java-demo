package com.mmwwtt.demo.mybatis.demo;

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
public class Contact  {
    private Long contactId;
    private String email;
    private String phoneNumber;
    private String address;
    private String userId;
}
