package com.lititi.exams.commons2.spring;

import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.PropertySources;
import org.springframework.stereotype.Component;

/**
 * 
 * @company 杭州利提提科技有限公司
 * @author Kevin
 * @version SpringContextHolder.java, v 0.1 May 30, 2019 3:27:40 PM
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    private static void setContext(ApplicationContext context) {
        SpringContextHolder.context = context;
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        SpringContextHolder.setContext(context);
    }

    public static Object getBean(String beanName) {
        return context.getBean(beanName);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        if (context instanceof ListableBeanFactory) {
            ListableBeanFactory listableBeanFactory = context;
            return listableBeanFactory.getBeansOfType(type);
        }
        return null;
    }

    public static <T> T getBean(String beanName, Class<T> beanClass) {
        return context.getBean(beanName, beanClass);
    }

    public static <T> T getBean(Class<T> requiredType) {
        return context.getBean(requiredType);
    }

    public static Object getBean(Class<?> requiredType, Object... args) {
        return context.getBean(requiredType, args);
    }

    public static String getBeanNameByClass(Class<?> clazz) {
        String[] names = context.getBeanNamesForType(clazz);
        if (ArrayUtils.isEmpty(names)) {
            return null;
        }
        return names[0];
    }

    public static String getProperty(String name) {
        PropertySources propertySources =
            context.getBean(PropertySourcesPlaceholderConfigurer.class).getAppliedPropertySources();
        for (PropertySource<?> source : propertySources) {
            String value = (String)source.getProperty(name);
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
