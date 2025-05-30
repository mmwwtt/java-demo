package com.mmwwtt.demo.se.file;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class FileTest {

    @Test
    @DisplayName("打开文件并追加写入")
    public void test1() throws IOException {
        String filePath = "src/main/resources/file/test.txt";

        File file = new File(filePath);
        // 检查文件的父目录是否存在，如果不存在则创建
        if (!file.getParentFile().exists()) {
            // 创建多级目录
            Boolean isSuccess = file.getParentFile().mkdirs();
        }

        //参数1， 文件路径， 文件不存在则自动创建
        //参数2： 是否以追加的方式写入
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            // 写入内容
            String content = "Hello, World!\n";
            fos.write(content.getBytes());
        } catch (IOException e) {
            log.info("error");
        }
    }

    @Test
    @DisplayName("通过文件路径读取文件内容")
    public void test2() throws IOException {
        String filePath = "src/main/resources/file/test.txt";
        Path path = Path.of(filePath);
        List<String> lines = Files.readAllLines(path, Charset.forName("GB2312"));
        log.info(lines.toString());
    }
}
