package com.mmwwtt.demo.se.基础;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class 编码问题 {

    /**
     * 不要在代码中用硬编码表示换行(\n),会车(/r),文件路径分隔符号(\ /)等
     * 在不同的操作系统下，该硬编码的可能存在不同的含义，当迁移到别的操作系统，会导致效果不一致
     *
     * 正则表达式中允许使用 \n \r
     * Java.nio.file.Paths 能够自动区分系统使用的文件路径分隔符，使用Paths构造文件路径时允许使用硬编码路径分隔符
     */
    @Test
    public void 硬编码字符() {
        //表示换行符 \r\n
        String str1 = System.lineSeparator();

        //表示文件路径分隔符 \
        String str2 = File.separator;

        //表示多路径字符分隔符 ;
        String str3 = File.pathSeparator;
    }

    /**
     * 字符和字节的相互转换时，也要指名编码方式
     * 因为java虚拟机的编码方式默认和操作系统的编码方式相同
     * 所以在不同平台间传输时，字符-字节-字符 的形式传输转换，会导致最后的结果和预期不同
     */
    @Test
    public void 字符和字节的转换() {
        String str = "hello world";
        byte[] buf = str.getBytes(StandardCharsets.UTF_8);
        String res = new String(buf, StandardCharsets.UTF_8);
    }
}
