package com.mmwwtt.demo.ee.lombok使用;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
public class LombokUser {

    private Long userId;
    private String userName;
    private int sex;
    private int age;

}