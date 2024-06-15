package com.mmwwtt.java.demo.se.规范;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class 规范1 {

    /**
     * 不要在一行中进行两次赋值(可读性差)
     * 反例： a = b++;
     */
    @Test
    public void 不要在一行中进行两次赋值() {
        int a = 0;
        int b = 1;
    }

    /**
     * 内存中的敏感信息使用完后应立即清除
     * 如果使用String获取密码信息使用后，变量会等待垃圾回收机制来处理，期间可能存在敏感信息泄露等
     * 推荐使用char[]/byte[]处理敏感字段
     */
    @Test
    public void 内存中的敏感信息使用完后应立即清除() {
        char[] password = new char[]{'1', '2', '3'};
        Arrays.fill(password, (char) 0x00);
    }

    /**
     * System.exit() 方法会结束正在运行的虚拟机，导致拒绝服务攻击
     * 要让main()方法正常推出结束进程
     *
     * 在命令行种使用System.exit()是允许的
     */
    @Test
    public void 不要调用System_exit() {
        System.exit(1);
        System.out.println("hello");
    }

    @Test
    public void 关闭资源() throws FileNotFoundException {
        System.out.println();
        try (FileInputStream in = new FileInputStream("resources/test.txt");
             FileOutputStream out = new FileOutputStream("resources/test.txt")) {

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @Test
    public void 禁止创建不必要的对象() {
        // 以下方式都创建了两个对象
        String str1 = new String("hell0");
        Integer num1 = new Integer(90);

        //需要改为以下方式
        String str2 = "hello";
        Integer num2 = Integer.valueOf(90);
    }

    /**
     * 对可能为null的对象进行空指针校验，防止出现空指针异常
     */
    @Test
    public void 空指针校验() {
        String str = null;
        if (str != null) {
            str.equals("hello");
        }
    }

    /**
     * 表达式中变量放左边，常量放右边
     */
    @Test
    public void 表达式比较() {
        int a = 0;
        if (a == Integer.MAX_VALUE) {
            System.out.println(a);
        }
    }

    /**
     * 适当的使用括号来明确运算顺序
     * 过于依赖运算符优先级，不便于代码理解
     */
    @Test
    public void 避免过分依赖运算符优先级() {
        if ((true && true) || (false && false)) {

        }

        int a = 1 << (2 + 3);

        int b = 3 == 2 ? 1 : (123 - 321);
    }

    /**
     * 不要改变入参，引用类型出外
     * 改变入参会增加理解难度，可以新增临时变量来替代
     */
    public void 不要改变入参(int cnt) {
        int tmpCnt = cnt;
    }
}
