package com.mmwwtt.demo.se.基础;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Slf4j
public class BaseDemoTest {

    /**
     * 日志记录不要使用System.out/System.err在控制台打印，需要使用对应的日志框架(slf4j等)
     * 日志类需要声明为 private static final
     */
    private static final Logger logger = Logger.getLogger(BaseDemoTest.class.getName());
    /**
     * 常量申明
     */
    public static final String href = "www.baidu.com";


    /**
     * 返回值类型为两个条件中长度大的
     * 三目运算符中的两个返回对象不能为null
     */
    @Test
    @DisplayName("三目运算符")
    public void test1() {
        char c = 'A';
        int num = 6500;
        boolean flag = true;
        int result = flag ? c: num;
        log.info("{}",result);
    }



    /**
     * 不要在代码中用硬编码(字符串)表示换行(\n),会车(/r),文件路径分隔符号(\ /)等
     * 不同系统中的换行转义符不同，windows(\n), linux(\r\n);
     */
    @Test
    @DisplayName("分隔符示例")
    public void test2() {
        //表示换行符 \r\n
        String str1 = System.lineSeparator();

        //表示文件路径分隔符 \
        String str2 = File.separator;

        //表示多路径字符分隔符 ;
        String str3 = File.pathSeparator;
    }

    /**
     * 字符和字节转换时，要指名编码
     * jvm编码方式和操作系统相同
     * 不同平台间传输时，字符->字节->字符 不指名编码，结果会不同
     */
    @Test
    @DisplayName("字符和字节转换")
    public void test3() {
        String str = "hello world";
        byte[] buf = str.getBytes(StandardCharsets.UTF_8);
        String res = new String(buf, StandardCharsets.UTF_8);
    }


    /**
     * 不要在finally块中使用return,break,continue语句，会导致非正常结束
     * 不能用空的catch块捕获异常，没有意义
     * 先捕获可预见的异常，最后捕获基类异常(Exception e)
     * 运行时异常(如NullPointException)不要捕获，尽量通过预检查处理
     * try中会把return的变量缓存起来
     * finally修改return变量中的内容，但是不能改变引用地址，类似引用传递
     */
    @Test
    @DisplayName("捕获异常")
    public void test4() throws Exception {
        try {
            System.out.println("执行代码");
        } catch (Exception e) {
            log.info("捕获到异常");
            throw new Exception("返回的异常信息");
        } finally {
            log.info("执行完成");
        }
    }


    //整形默认int类型, 浮点型默认double类型
    private long a = 1L;
    private float f = 1F;
    private double d1 = 1D;

    //大类型转小类型要显示声明
    float num3 = (float) d1;

    @Test
    @DisplayName("数据比较大小")
    public void test5() {
        int num1 = 1;
        int num2 = 2;
        //基本数据类型 == 判断值是否相等
        log.info("{}", num1 == num2);

        Integer num3 = 1;
        Integer num4 = 2;
        //包装类型通quals()判断值是否相等
        log.info("{}", num3.equals(num4));
    }

    @Test
    public void test6() {
        // 浮点基本类型 不能用 == 判断,二进制除不尽，存在误差，要改为BigDecimal做精确计算
        // 浮点包装类型不能用 equals判断 ,能用compareTo比较大小
        Double num5 = 0.3;
        Double num6 = 0.3;
        log.info("{}", num5.compareTo(num6));
    }


    /**
     * switch语句
     */
    @Test
    @DisplayName("switch使用")
    public void test7() {
        String str = "a";
        switch (str) {
            case "a":
            case "b":
                log.info("b");
            case "c":
                log.info("c");
            case "d":
                log.info("d");
                break;
            default:
                log.info("default");
                break;
        }
    }

    /**
     * continue A: 跳出标签的当层循环
     * break    A：跳出标签的循环
     */
    @Test
    @DisplayName("label标签跳出多层循环")
    public void test8() {
        A: for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 2) {
                    continue A;
                }
                if (i == 4) {
                    break A;
                }
            }
            log.info("{}",i);
        }
    }
}
