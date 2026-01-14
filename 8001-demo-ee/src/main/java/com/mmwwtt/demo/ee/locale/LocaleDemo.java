package com.mmwwtt.demo.ee.locale;

import com.mmwwtt.demo.se.thread.GlobalThreadPool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

public class LocaleDemo {

    private final ThreadPoolExecutor pool = GlobalThreadPool.getCpuThreadPool();

    @Test
    @DisplayName("获取当前语言环境")
    public void test1() {

        //获取当前线程的语言环境
        Locale locale =LocaleContextHolder.getLocale();
        String lang = locale.getLanguage();
        if(Objects.equals(locale.getLanguage(), Locale.SIMPLIFIED_CHINESE.getLanguage())) {

        }


        //开启子线程后需要将Locale 塞进子线程
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            LocaleContextHolder.setLocale(locale);
           System.out.println("1");
        }, pool);

    }
}
