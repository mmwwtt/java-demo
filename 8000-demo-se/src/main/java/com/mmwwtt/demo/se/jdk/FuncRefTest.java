package com.mmwwtt.demo.se.jdk;

import com.mmwwtt.demo.common.vo.BaseInfoVO;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * 方法引用demo
 */
public class FuncRefTest {

    @Test
    public void funcRefDemo() {
        List<BaseInfoVO> list = BaseInfoVO.getPresetList();

        //普通方法引用(不带参数)
        list.forEach(BaseInfoVO::format);

    }

}
