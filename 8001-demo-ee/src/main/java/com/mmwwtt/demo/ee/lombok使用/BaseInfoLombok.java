package com.mmwwtt.demo.ee.lombok使用;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@RequiredArgsConstructor
public class BaseInfoLombok {


    private String baseInfoId;
    private String name;

    private String sex_code;

    private Double height;

    private LocalDate birthDay;


    private LocalDateTime createdDate;


    private LocalDateTime lastUpdatedDate;

}