package com.mmwwtt.demo.ee.mockito;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;

import static org.mockito.Mockito.*;

public class MockitoTest {
    //模拟对象注入到被测试类中
    @InjectMocks
    private UserService userService;

    //mock创建模拟对象
    private List<String> list1 = mock(List.class);
    @Mock
    private List<String> list2;

    //spy部分模拟对象，可调用真实方法
    private List<String> spyList1 = spy(List.class);
    @Spy
    private List<String> spyList2;

    @Test
    public void demo() {
        //为对象的方法调用指定返回值
        when(list1.get(0)).thenReturn("first");
        when(list1.get(anyInt())).thenReturn("first");
        System.out.println(list1.get(0));

        //验证方法是否被调用过
        list1.add("one");
        verify(list1).add("one");
    }
}
