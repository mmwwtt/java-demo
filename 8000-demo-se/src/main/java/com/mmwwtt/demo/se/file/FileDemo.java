package com.mmwwtt.demo.se.file;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileDemo {

    /**
     * 打开文件并追加写入
     */
    @Test void test() throws IOException {
        String filePath = "src/main/resources/file/test.txt";

        File file = new File(filePath);
        // 检查文件的父目录是否存在，如果不存在则创建
        if (!file.getParentFile().exists()) {
            // 创建多级目录
            file.getParentFile().mkdirs();
        }

        //参数1， 文件路径， 文件不存在则自动创建
        //参数2： 是否以追加的方式写入
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            // 写入内容
            String content = "Hello, World!\n";
            fos.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
