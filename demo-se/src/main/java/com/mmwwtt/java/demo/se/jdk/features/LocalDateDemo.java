package com.mmwwtt.java.demo.se.jdk.features;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateDemo {

    @Test
    public void test() {
        LocalDateTime localDateTime = LocalDateTime.now();
        System.out.println(localDateTime);
    }

    @Test
    public void dateFormatDemo() {
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
        String formatStr = localDateTime.format(dateTimeFormatter);
        System.out.println(formatStr);
    }

    @Test
    public void timeAddDemo() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.plusHours(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
        String formatStr = localDateTime.format(dateTimeFormatter);
        System.out.println(formatStr);
    }

    @Test
    public void timeMinusDemo() {
        LocalDateTime localDateTime = LocalDateTime.now();
        localDateTime.minusHours(1);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
        String formatStr = localDateTime.format(dateTimeFormatter);
        System.out.println(formatStr);
    }
}
