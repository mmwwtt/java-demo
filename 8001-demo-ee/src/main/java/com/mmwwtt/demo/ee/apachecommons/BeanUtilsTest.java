package com.mmwwtt.demo.ee.apachecommons;

import com.mmwwtt.demo.common.entity.BaseInfo;
import com.mmwwtt.demo.common.vo.BaseInfoVO;
import com.mmwwtt.demo.common.vo.FamilyVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class BeanUtilsTest {
    @Test
    @DisplayName("测试BeanUtils")
    public void test() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        BaseInfoVO vo = BaseInfoVO.getInstance();
        BaseInfo baseInfo = new BaseInfo();

        //复制对象所有属性
        BeanUtils.copyProperties(baseInfo, vo);
        //复制对象指定的属性
        BeanUtils.copyProperty(baseInfo, "baseInfoId", vo.getBaseInfoId());

        //根据字段名获得属性
        String str = BeanUtils.getProperty(vo, "baseInfoId");
        Long baseInfoId = Long.valueOf(BeanUtils.getProperty(vo, "baseInfoId"));

        //根据字段名设置属性
        PropertyUtils.setProperty(vo, "sexCode", "1");

        //嵌套属性访问
        String email = BeanUtils.getProperty(vo, "contact.email");
        FamilyVO family = (FamilyVO) PropertyUtils.getProperty(vo, "familyList[0]");

        //集合属性
        Map<String, String> properties = new HashMap<>();
        properties.put("name", "欢欢");
        BeanUtils.populate(vo, properties);
        log.info("");
    }
}
