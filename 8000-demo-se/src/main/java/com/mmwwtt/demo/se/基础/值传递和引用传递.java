package com.mmwwtt.demo.se.基础;


import com.mmwwtt.demo.common.vo.BaseInfoVO;
import org.junit.jupiter.api.Test;

public class 值传递和引用传递 {

    /**
     * 值传递传的是值本身(基本数据类型)
     * 引用传递传的是地址值(其他类型)
     */
    public void fun(int num, BaseInfoVO baseInfoVO1, BaseInfoVO baseInfoVO2) {
        //值传递，不会改变原值
        num = 3;

        //引用传递，修改引用地址上的对象
        //baseInfoVO1.setAge(18);

        //引用传递，point2指向了新的引用地址，不会对原地址上的对象造成影响
        baseInfoVO2= BaseInfoVO.getInstance();
    }

    @Test
    public void test() {
        int num = 1;
        BaseInfoVO baseInfoVO1 = BaseInfoVO.getInstance();
        BaseInfoVO baseInfoVO2 = BaseInfoVO.getInstance();

        fun(num, baseInfoVO1, baseInfoVO2);
    }
}
