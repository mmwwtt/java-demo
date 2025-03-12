package com.mmwwtt.demo.ddd.interfaces.api;

import com.mmwwtt.demo.ddd.infrastructure.dto.lookuptype.LookupTypeDTO;
import com.mmwwtt.demo.ddd.infrastructure.dto.lookuptype.LookupTypeSaveDTO;
import com.mmwwtt.demo.ddd.infrastructure.utils.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/lookupType")
@Tag(name = "lookupType服务相关API")
public interface LookupTypeAPI {

    @PostMapping("/")
    @Operation(summary = "新增lookupType")
    ResponseData<LookupTypeDTO> add(LookupTypeSaveDTO lookupTypeSaveDTO);

}
