package com.mmwwtt.demo.ee.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.mmwwtt.demo.common.entity.BaseInfo;
import org.junit.jupiter.api.Test;

public class KryoTest {

    @Test
    public void test() {
        Kryo kryo = new Kryo();
        kryo.setRegistrationRequired(false); // 允许未注册的类

        BaseInfo baseInfo = BaseInfo.getInstance();
        BaseInfo baseInfo1 = kryo.copy(baseInfo);   // 深拷贝，无需序列化到字节
        BaseInfo baseInfo2 = kryo.copyShallow(baseInfo);   // 浅拷贝
        System.out.println(baseInfo);
    }
}
