package com.mmwwtt.demo.common.dto;

import lombok.Data;

import java.time.LocalDate;

@Data

public class FamilyDTO {
    private Long familyId;
    private String name;
    private String sexCode;
    private LocalDate birthday;
}
