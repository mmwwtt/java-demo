package com.mmwwtt.demo.se.基础;


import com.mmwwtt.demo.common.vo.BaseInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Slf4j
public class 值传递和引用传递 {

    /**
     * 日志记录不要使用System.out/System.err在控制台打印，需要使用对应的日志框架(slf4j等)
     * 日志类需要声明为 private static final
     */
    private static final Logger logger = Logger.getLogger(值传递和引用传递.class.getName());
    /**
     * 常量申明
     */
    public static final String href = "www.baidu.com";


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



    /**
     * 返回值类型为两个条件中长度大
     * 三目运算符中的两个返回对象不能为null
     */
    @Test
    public void 三目运算符() {
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
    public void 分隔符示例() {
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
    public void 字符和字节的转换() {
        String str = "hello world";
        byte[] buf = str.getBytes(StandardCharsets.UTF_8);
        String res = new String(buf, StandardCharsets.UTF_8);
    }


    /**
     * 不要在finally块中使用return,break,continue语句，会导致非正常结束
     * 不能用空的catch块捕获异常，没有意义
     * 先捕获可预见的异常，最后捕获基类异常(Exception e)
     * 可通过预检查进行处理的运行时异常(如NullPointException)不要捕获，尽量通过预检查处理
     * try中会把return的变量缓存起来
     * finally修改return变量中的内容，但是不能改变引用地址，类似引用传递
     */
    @Test
    public void 捕获异常() {
        try {
            throw new Exception("返回的异常信息");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }

    @Test
    public void 向上抛出异常() throws Exception {
        try {
            throw new Exception("返回的异常信息");
        } finally {

        }
    }
}
