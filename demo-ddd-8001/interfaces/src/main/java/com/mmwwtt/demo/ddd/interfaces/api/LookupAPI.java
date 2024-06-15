package com.mmwwtt.demo.ddd.interfaces.api;

import com.mmwwtt.demo.ddd.infrastructure.dto.lookup.LookupDTO;
import com.mmwwtt.demo.ddd.infrastructure.dto.lookup.LookupSaveDTO;
import com.mmwwtt.demo.ddd.infrastructure.utils.ResponseData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/service/lookup")
@Tag(name = "lookup服务相关API")
public interface LookupAPI {

    @PostMapping("/")
    @Operation(summary = "新增lookup")
    ResponseData<LookupDTO> add(@Valid @RequestBody LookupSaveDTO lookupSaveDTO);

    @PostMapping("/getByQuery")
    @Operation(summary = "查询lookup")
    ResponseData<LookupDTO> getByQuery(@RequestParam(value = "lookupValue", required = false) String lookupValue,
                                       @RequestParam(value = "lastUpdateDateTo", required = false) Date lastUpdateDateTo,
                                       @RequestParam(value = "lastUpdateDateFrom", required = false) Date lastUpdateDateFrom);
}
