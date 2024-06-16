package com.mmwwtt.demo.se.file;

import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class FileDemo {

    /**
     * 打开文件并追加写入
     */
    @Test void test() throws IOException {
        //参数1， 文件路径， 项目根目录中 找不到指定文件，则会自动创建
        //参数2： 是否以追加的方式写入
        FileOutputStream file = new FileOutputStream("src/main/resources/test.txt", true);
        byte[] bs = String.format("current: %s\n", new Date()).getBytes(StandardCharsets.UTF_8);
        file.write(bs);
        file.close();
    }
}
