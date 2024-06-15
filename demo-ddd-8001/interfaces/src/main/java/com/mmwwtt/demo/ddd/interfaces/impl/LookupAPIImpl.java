package com.mmwwtt.demo.ddd.interfaces.impl;

import com.mmwwtt.demo.ddd.interfaces.api.LookupAPI;
import com.mmwwtt.demo.ddd.application.impl.LookupServiceImpl;
import com.mmwwtt.demo.ddd.domain.entity.Lookup;
import com.mmwwtt.demo.ddd.infrastructure.converter.LookupEntityToDTOConverter;
import com.mmwwtt.demo.ddd.infrastructure.converter.LookupSaveDTOToEntityConverter;
import com.mmwwtt.demo.ddd.infrastructure.dto.lookup.LookupDTO;
import com.mmwwtt.demo.ddd.infrastructure.dto.lookup.LookupSaveDTO;
import com.mmwwtt.demo.ddd.infrastructure.utils.ResponseData;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;


@RestController
public class LookupAPIImpl implements LookupAPI {

    @Resource
    private LookupServiceImpl lookupService;

    @Override
    public ResponseData<LookupDTO> add(LookupSaveDTO lookupSaveDTO) {
        Lookup lookup = LookupSaveDTOToEntityConverter.INSTANCE.convert(lookupSaveDTO);
        lookupService.add(lookup);
        LookupDTO resultDTO = LookupEntityToDTOConverter.INSTENCE.convert(lookup);
        return new ResponseData<LookupDTO>().success(resultDTO);
    }

    @Override
    public ResponseData<LookupDTO> getByQuery(String lookupValue, Date lastUpdateDateTo, Date lastUpdateDateFrom) {
        return null;
    }

//    @Override
//    public ResponseData<LookupDTO> getByQuery(String lookupValue) {
//   //     Lookup lookup = LookupSaveDTOToEntityConverter.INSTANCE.convert(lookupSaveDTO);
////        List<Lookup>
//        return null;
//    }

}
