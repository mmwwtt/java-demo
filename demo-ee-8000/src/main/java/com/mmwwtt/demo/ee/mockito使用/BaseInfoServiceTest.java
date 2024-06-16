package com.mmwwtt.demo.ee.mockito使用;

import com.mmwwtt.demo.ee.demo.domain.dao.BaseInfoDao;
import com.mmwwtt.demo.ee.demo.domain.entity.BaseInfo;
import com.mmwwtt.demo.ee.demo.domain.service.BaseInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

public class BaseInfoServiceTest {
    @Mock
    private BaseInfoDao baseInfoDao;

    @InjectMocks
    private BaseInfoService baseInfoService;

    
    @Test
    public void should_addBaseInfo_success() {
        baseInfoService.addBaseInfo(new BaseInfo());
    }
}
