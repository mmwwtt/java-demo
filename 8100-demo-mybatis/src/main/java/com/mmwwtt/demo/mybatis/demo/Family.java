package com.mmwwtt.demo.mybatis.demo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Family {
    private Long familyId;
    private String name;
    private Long userId;
}
