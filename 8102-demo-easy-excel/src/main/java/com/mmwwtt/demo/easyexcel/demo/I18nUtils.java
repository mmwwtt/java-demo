package com.mmwwtt.demo.easyexcel.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public final class I18nUtils {


    private static MessageSource messageSource1;

    //构造方式注入static属性
    @Autowired
    public I18nUtils(MessageSource messageSource) {
        I18nUtils.messageSource1 = messageSource;
    }

    //set方式注入static属性
    @Autowired
    private void setMessageSource(MessageSource messageSource) {
        I18nUtils.messageSource1 = messageSource;
    }

    @Autowired
    private MessageSource messageSource;

    public  String get(String code, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(code, args, locale);
        }catch (Exception e) {
            return code;
        }
    }

    public  String get(String code) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            return messageSource.getMessage(code,new Object[0],locale);
        }catch (Exception e) {
            return code;
        }
    }

    public String getLanguage() {
        return LocaleContextHolder.getLocale().getLanguage();
    }
}