package com.lititi.exams.commons2.utils;

import com.lititi.exams.commons2.exception.LttException;
import com.lititi.exams.commons2.log.LttLogger;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

/**
 * 
 * @company 杭州利提提科技有限公司
 * @author Kevin
 * @version ClassUtil.java, v 0.1 May 30, 2019 3:33:41 PM
 */
public class ClassUtil {

    /**
     * 统一日志处理
     */
    private static final LttLogger log = new LttLogger(ClassUtil.class);

    private ClassUtil() {

    }

    public static Object instance(String className) {
        try {
            if (className == null || className.length() == 0) {
                return null;
            }
            Class<?> clazz = Class.forName(className);
            return clazz.newInstance();
        } catch (Exception e) {
            throw new LttException(e);
        }
    }

    public static Object getBeanFieldValue(Object obj, String fieldName) throws Exception {
        Class<?> clazz = obj.getClass();
        Field field = getField(clazz, fieldName);
        if (field == null) {
            throw new LttException("Can not find field : \"" + fieldName + "\" during " + obj.getClass().getName());
        }
        return getBeanFieldValue(obj, field);
    }

    public static Object getBeanFieldValue(Object obj, Field field) throws Exception {
        Class<?> clazz = obj.getClass();
        if (field == null) {
            return null;
        }
        Method method = getBeanGetMethod(clazz, field);
        if (method == null) {
            return null;
        }
        return method.invoke(obj);
    }

    public static Object invokeMethod(Object obj, String methodName, Class<?>[] paramTypes, Object[] params)
        throws Exception {
        Class<?> clazz = obj.getClass();
        Method m = clazz.getMethod(methodName, paramTypes);
        return m.invoke(obj, params);
    }

    public static void setFieldValue(Object obj, String fieldName, Object fieldValue) {
        if (obj == null) {
            return;
        }
        Class<?> clazz = obj.getClass();
        Field field = getField(clazz, fieldName);
        setFieldValue(obj, field, fieldValue);
    }

    public static void setFieldValue(Object obj, Field field, Object fieldValue) {
        if (obj == null) {
            return;
        }
        try {
            Class<?> clazz = obj.getClass();
            Class<?> fieldClazz = field.getType();
            Method method = clazz.getMethod("set" + CommonUtil
                .transFirstLetter(field.getName(), true), fieldClazz);
            method.invoke(obj, parseTypeValue(fieldValue, fieldClazz.getSimpleName()));
        } catch (Exception e) {
            log.error("set field value error : " + obj.getClass().getSimpleName() + "." + field.getName());
            throw new LttException(e);
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Class<?> current = clazz;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (Exception e) {
            }
            current = current.getSuperclass();
        }
        return null;
    }

    public static Method getBeanGetMethod(Class<?> clazz, Field field) {
        try {
            String prefix = field.getType().equals(boolean.class) ? "is" : "get";
            String fieldName = field.getName();
            return clazz.getMethod(prefix + CommonUtil.transFirstLetter(fieldName, true));
        } catch (Exception e) {
        }
        return null;
    }

    public static Object parseTypeValue(Object value, String type) {
        if (value == null) {
            return null;
        }
        if ("String".equals(type)) {
            return value.toString();
        }
        if ("int".equals(type)) {
            return Integer.parseInt(value.toString());
        }
        if ("Integer".equals(type)) {
            return Integer.valueOf(value.toString());
        }
        if ("long".equals(type)) {
            return Long.parseLong(value.toString());
        }
        if ("Long".equals(type)) {
            return new Long(value.toString());
        }
        if ("float".equals(type)) {
            return Float.parseFloat(value.toString());
        }
        if ("Float".equals(type)) {
            return new Float(value.toString());
        }
        if ("double".equals(type)) {
            return Double.parseDouble(value.toString());
        }
        if ("Double".equals(type)) {
            return new Double(value.toString());
        }
        if ("boolean".equals(type)) {
            return Boolean.parseBoolean(value.toString());
        }
        if ("Boolean".equals(type)) {
            return Boolean.valueOf(value.toString());
        }
        if ("Date".equals(type) && !(value instanceof Date)) {
            return DateUtil.getFormattedDate(value.toString());
        }
        return value;
    }

    public static Field[] getAllFields(Class<?> clazz) {
        Class<?> temp = clazz;
        Map<String, Field> fields = new LinkedHashMap<String, Field>();
        while (temp != null) {
            Field[] fs = getFields(temp);
            for (int i = 0; i < fs.length; i++) {
                Field f = fs[i];
                if (f.getName().startsWith("this$")) {
                    continue;
                }
                if (fields.get(f.getName()) == null) {
                    fields.put(f.getName(), f);
                }
            }
            temp = temp.getSuperclass();
        }
        return fields.values().toArray(new Field[fields.size()]);
    }

    public static Field[] getFields(Class<?> clazz) {
        clazz.getFields();
        return clazz.getDeclaredFields();
    }

    public static ClassLoader ceateClassLoader(String[] classpaths, ClassLoader parentLoader) {
        List<URL> list = new ArrayList<URL>();
        for (int i = 0; i < classpaths.length; i++) {
            try {
                list.add(new URL(computeForURLClassLoader(classpaths[i])));
            } catch (MalformedURLException e) {
                log.error(e.getMessage());
            }
        }
        return new URLClassLoader(list.toArray(new URL[list.size()]), ClassUtil.class.getClassLoader());
    }

    private static String computeForURLClassLoader(String classpath) {
        if (!classpath.endsWith("/")) {
            File file = new File(classpath);
            if (file.exists() && file.isDirectory()) {
                classpath = classpath.concat("/");
            }
        }
        return classpath;
    }

    public static boolean isComplex(Class<?> clazz) {
        if (clazz.isArray()) {
            return true;
        }
        if (Date.class.isAssignableFrom(clazz)) {
            return false;// Date当做简单类型处理
        }
        return clazz.getPackage() != null && !clazz.getName().startsWith("java.lang.");
    }
}
