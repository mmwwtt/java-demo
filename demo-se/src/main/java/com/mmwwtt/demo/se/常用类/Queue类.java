package com.mmwwtt.demo.se.常用类;


import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.Queue;

public class Queue类 {

    @Test
    public void queue类常用方法() {
        Queue<Integer> queue1 = new LinkedList<>();
        Queue<Integer> queue2 = new LinkedList<>();

        //入队，
        queue1.add(1);
        //offer():队列已经满会抛出异常
        queue2.offer(2);

        //返回队首元素
        queue1.peek();
        //队首为空会抛出异常
        queue2.element();

        //将队首元素出队，并返回,
        queue2.poll();
        //remove():队列为空会返回异常
        queue1.remove();

        //返回队长
        queue1.size();

        // 判断队列是否为空
        queue1.isEmpty();


        //清空队列
        queue1.clear();
    }
}
