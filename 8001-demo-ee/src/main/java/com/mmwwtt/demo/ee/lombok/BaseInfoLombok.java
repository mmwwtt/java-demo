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
@Data
public class BaseInfoLombok {
    private Long baseInfoId;
    private String name;
    private String sexCode;
    private Double height;
    private LocalDate birthDate;
}