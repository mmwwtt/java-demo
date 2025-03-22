package com.mmwwtt.demo.se.常用类;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class StackDemo {

    @Test
    public void stack类常用方法() {
        Stack<Integer> stack = new Stack<>();

        //入栈
        stack.push(1);

        //返回栈顶元素,如果栈为空调用peek()会报错，用之前要判断是否为空
        stack.peek();

        //删除栈顶元素，并返回
        stack.pop();

        //栈是否为空,return boolean
        stack.isEmpty();

        //返回栈长度
        stack.size();

        //判断栈是否为空
        stack.isEmpty();

        //返回是否包含元素
        stack.search(1);

        //添加元素
        stack.add(1);
        stack.add(1,1);
        //添加Collectioni接口的集合元素
        List<Integer> list = new ArrayList<>();
        list.add(1);
        stack.addAll(list);
    }
}
