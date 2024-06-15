package com.mmwwtt.demo.ddd.infrastructure.dto.lookuptype;

import com.mmwwtt.demo.ddd.infrastructure.dto.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookupTypeSaveDTO extends BaseDTO {

    @NotBlank(message = "type 不能为空")
    private String type;

    @NotBlank(message = "类型中文名 不能为空")
    private String nameCN;

    @NotBlank(message = "类型英文名 不能为空")
    private String nameEN;

    private String note;
}
