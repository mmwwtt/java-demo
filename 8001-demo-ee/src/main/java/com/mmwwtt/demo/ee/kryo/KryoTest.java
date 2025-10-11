package com.mmwwtt.demo.ee.kryo;

import com.mmwwtt.demo.common.entity.BaseInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Kryo : 深拷贝工具类(序列化-反序列化中最快的工具类)
 */
public class KryoTest {

    @Test
    public void test() {
        List<BaseInfo> list = BaseInfo.getPresetList();

        List<BaseInfo> copyList = KryoUtil.deepCopy(list);
        copyList.get(0).setName("tttt");
        System.out.println();
    }
}
