package com.mmwwtt.demo.ddd.infrastructure.dto.lookup;

import com.mmwwtt.demo.ddd.infrastructure.dto.BaseDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookupSaveDTO extends BaseDTO {

    @NotBlank(message = "lookup类型 不能为空")
    Long lookupTypeId;

    @NotBlank(message = "lookup编码 不能为空")
    String code;

    @NotBlank(message = "lookup中文名 不能为空")
    String nameCN;

    @NotBlank(message = "lookup英文名 不能为空")
    String nameEN;

    String note;

}
