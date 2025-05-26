package com.mmwwtt.demo.ddd.application.impl;

import com.mmwwtt.demo.ddd.application.service.LookupTypeService;
import com.mmwwtt.demo.ddd.domain.dao.LookupTypeDao;
import com.mmwwtt.demo.ddd.domain.entity.LookupType;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class LookupTypeServiceImpl implements LookupTypeService {

    @Resource
    private LookupTypeDao lookupTypeDao;

    @Override
    public LookupType add(LookupType lookupType) {
        lookupType.setCreateById(Long.valueOf(1));
        lookupType.setCreateByName("西门吹雪");
        lookupType.setCreateDate(new Date());
        lookupType.setLastUpdateById(Long.valueOf(1));
        lookupType.setLastUpdateByName("西门吹雪");
        lookupType.setLastUpdateDate(new Date());
        lookupType.setDeleteFlag("N");
        lookupTypeDao.insert(lookupType);
        return lookupType;
    }
}
