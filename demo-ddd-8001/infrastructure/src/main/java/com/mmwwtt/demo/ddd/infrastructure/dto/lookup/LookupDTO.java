package com.mmwwtt.demo.ddd.infrastructure.dto.lookup;

import com.mmwwtt.demo.ddd.infrastructure.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LookupDTO extends BaseDTO {

    Long lookupId;

    Long lookup_type_id;

    String code;

    String name;

    String note;

}
