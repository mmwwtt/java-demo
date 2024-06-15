package com.mmwwtt.java.demo.se;


import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class 文件使用 {

    @Test
    public void 获得当前文件路径() {
        System.out.println(System.getProperty("user.dir"));//user.dir指定了当前的路径
    }

    @Test void 获得项目根路径() throws IOException {
        System.out.println(new File("").getCanonicalPath());
    }

    @Test void test() throws IOException {
        //参数1， 文件路径， 项目根目录中 找不到指定文件，则会自动创建
        //参数2： 是否以追加的方式写入
        FileOutputStream file = new FileOutputStream("resources/test.txt", true);
        byte[] bs = String.valueOf(new Date()).getBytes(StandardCharsets.UTF_8);
        file.write(bs);
        file.close();
    }
}
