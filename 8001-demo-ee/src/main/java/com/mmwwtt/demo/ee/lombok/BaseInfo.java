package com.mmwwtt.demo.ee.lombok;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString(callSuper = true)  //callSuper= true   打印时父类属性也打印
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseInfo {
    private Long baseInfoId;
    private String name;
    private String sexCode;
    private Double height;
    private LocalDate birthDate;

    //lombok默认为boolean自动生成isXX方法
    private boolean show;

    public static void main(String[] args) {
        BaseInfo baseInfo = new BaseInfo();
        baseInfo.isShow();
    }
}