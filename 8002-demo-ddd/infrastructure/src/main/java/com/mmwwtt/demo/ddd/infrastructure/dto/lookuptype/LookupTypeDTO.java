package com.mmwwtt.demo.ddd.infrastructure.dto.lookuptype;

import com.mmwwtt.demo.ddd.infrastructure.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookupTypeDTO extends BaseDTO {


    private Long typeId;


    private String type;


    private String name;


    private String note;
}
