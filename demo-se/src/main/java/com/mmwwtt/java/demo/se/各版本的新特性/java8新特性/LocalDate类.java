package com.mmwwtt.java.demo.se.各版本的新特性.java8新特性;

import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;

import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.plaf.synth.SynthTextAreaUI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDate类 {

    @Test
    public void test() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
    }

    @Test
    public void 格式化时间() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
        String formatStr = localDateTime.format(dateTimeFormatter);
        System.out.println(formatStr);
    }

    @Test
    public void 增加() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plusHours(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
        String formatStr = localDateTime.format(dateTimeFormatter);
        System.out.println(formatStr);
    }

    @Test
    public void 减少() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.minusHours(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
        String formatStr = localDateTime.format(dateTimeFormatter);
        System.out.println(formatStr);
    }
}
