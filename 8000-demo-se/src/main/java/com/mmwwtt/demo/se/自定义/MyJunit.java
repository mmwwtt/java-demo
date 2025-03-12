package com.mmwwtt.demo.se.自定义;

import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Method;

/**
 * 自定义注解
 */
//自定义一个注解
@Retention(RetentionPolicy.RUNTIME)
public @interface MyJunit {

}

@Slf4j
class PrintNumber {
    public void showOdd(){  //打印奇数
        for (int i = 0; i < 10; i++) {
            if (i % 2!=0) {
                log.info(i+" ");
            }
        }
    }

    @MyJunit
    public void showEven(){ //打印偶数
        for (int i = 0; i < 10; i++) {
            if (i % 2==0) {
                log.info(i+" ");
            }
        }
    }
}

class JunitTest {
    public static void main(String[] args) throws Exception {
        //1.创建PrintNumber对象
        PrintNumber printNumber = new PrintNumber();
        //2.获取该类的字节码文件对象
        Class<? extends PrintNumber> clazz = printNumber.getClass();
        //3.获取对象中的所有方法
        Method[] methods = clazz.getMethods();
        for (Method method:methods) {
            //4.判断方法上是否有@MyJunit注解
            boolean flag = method.isAnnotationPresent(MyJunit.class);
            //5.如果方法上有@MyJunit注解，则执行
            if (flag){
                method.invoke(printNumber);
            }
        }
    }
}