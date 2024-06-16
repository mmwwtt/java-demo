package com.mmwwtt.demo.ee.lombok使用;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
public class LombokUser {

    private Long userId;
    private String userName;
    private int sex;
    private int age;

}