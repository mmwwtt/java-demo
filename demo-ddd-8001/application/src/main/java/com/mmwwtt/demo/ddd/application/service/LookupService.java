package com.mmwwtt.demo.ddd.application.service;


import com.mmwwtt.demo.ddd.domain.entity.Lookup;

import java.util.List;

public interface LookupService {
    Lookup add(Lookup lookup);

    Lookup getById(Long lookupId);

    List<Lookup> getByQuery(Lookup lookup);
}
