package com.mmwwtt.demo.ee.lombok;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BaseInfoLombok {


    private String baseInfoId;
    private String name;

    private String sex_code;

    private Double height;

    private LocalDate birthDate;


}