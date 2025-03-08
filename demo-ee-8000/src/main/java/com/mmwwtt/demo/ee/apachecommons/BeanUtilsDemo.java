package com.mmwwtt.demo.ee.apachecommons;

import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BeanUtilsDemo {
    @Test
    public void testDemo() throws InvocationTargetException, IllegalAccessException {
        BaseInfoVO vo = new BaseInfoVO();
        vo.setBaseInfoId(111L);
        BaseInfo baseInfo = new BaseInfo();
        BeanUtils.copyProperties(vo, baseInfo);

        Map<String, String> properties = new HashMap<>();
        properties.put("propertyName1", "value1");
        properties.put("propertyName2", "value2");
        BeanUtils.populate(vo, properties);
    }
}
