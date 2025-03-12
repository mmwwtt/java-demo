package com.mmwwtt.demo.se.泛型;


import org.junit.jupiter.api.Test;

public class 泛型使用 {

    /**
     * 泛型使用
     */
    @Test
    public void 泛型使用() {
        Message<String> message1 = new Message<>();
        String str1 = message1.getDate("");

        Message<Integer> message2 = new Message<>();
        Integer num = message2.getDate(22);
    }

    /**
     * 泛型限定符
     * <? extends T>: 生产者，不能使用add方法，只能从集合中get元素(用T及T的父类接收)
     * <? super T>: 消费者，不能使用get方法，只能往集合中add元素(T及T的子类),读取时要强转
     */
    public void 通过限定符限制可用的泛型类型() {
//        List<? extends B> bList1 = Arrays.asList(new B());
//        //用T及T的父类接收
//        A a = bList1.get(0);
//
//
//        List<? super B> bList2= new ArrayList<>();
//        //放入T及T的子类
//        bList2.add(new C());
//        B b = (B) bList2.get(1);
    }


}
