package com.mmwwtt.demo.ddd.application.impl;

import com.mmwwtt.demo.ddd.application.service.LookupService;
import com.mmwwtt.demo.ddd.domain.dao.LookupDao;
import com.mmwwtt.demo.ddd.domain.entity.Lookup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class LookupServiceImpl implements LookupService {

    @Autowired
    private LookupDao lookupDao;


    @Override
    public Lookup add(Lookup lookup) {
        lookup.setCreateById(Long.valueOf(1));
        lookup.setCreateByName("西门吹雪");
        lookup.setCreateDate(new Date());
        lookup.setLastUpdateById(Long.valueOf(1));
        lookup.setLastUpdateByName("西门吹雪");
        lookup.setLastUpdateDate(new Date());
        lookup.setDeleteFlag("N");
        lookupDao.insert(lookup);
        return lookup;
    }

    @Override
    public Lookup getById(Long lookupId) {
        return lookupDao.selectById(lookupId);
    }

    @Override
    public List<Lookup> getByQuery(Lookup lookup) {
        return lookupDao.getByQuery(lookup);
    }
}
