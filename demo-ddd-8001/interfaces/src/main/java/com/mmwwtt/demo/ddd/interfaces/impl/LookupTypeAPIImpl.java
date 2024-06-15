package com.mmwwtt.demo.ddd.interfaces.impl;

import com.mmwwtt.demo.ddd.interfaces.api.LookupTypeAPI;
import com.mmwwtt.demo.ddd.application.service.LookupTypeService;
import com.mmwwtt.demo.ddd.domain.entity.LookupType;
import com.mmwwtt.demo.ddd.infrastructure.converter.LookupTypeEntityToDTOConverter;
import com.mmwwtt.demo.ddd.infrastructure.converter.LookupTypeSaveDTOToEntityConverter;
import com.mmwwtt.demo.ddd.infrastructure.dto.lookuptype.LookupTypeDTO;
import com.mmwwtt.demo.ddd.infrastructure.dto.lookuptype.LookupTypeSaveDTO;
import com.mmwwtt.demo.ddd.infrastructure.utils.ResponseData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LookupTypeAPIImpl implements LookupTypeAPI {

    @Resource
    private LookupTypeService lookupTypeService;

    @Override
    public ResponseData<LookupTypeDTO> add(LookupTypeSaveDTO lookupTypeSaveDTO)     {
        LookupType lookupType = LookupTypeSaveDTOToEntityConverter.INSTANCE.convert(lookupTypeSaveDTO);
        lookupTypeService.add(lookupType);
        LookupTypeDTO resultDTO = LookupTypeEntityToDTOConverter.INSTANCE.convert(lookupType);
        return new ResponseData<LookupTypeDTO>().success(resultDTO);
    }
}
