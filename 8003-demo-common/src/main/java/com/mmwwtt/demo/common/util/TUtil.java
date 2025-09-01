package com.mmwwtt.demo.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TUtil {
    public static <T> T getValueByFields(JSONObject object, List<String> fields, Class<T> clazz) {
        if (Objects.isNull(object)) {
            return null;
        }
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }
        String lastKey = fields.get(fields.size() - 1);
        for (int i = 0; i < fields.size() - 1; i++) {
            if (!object.containsKey(fields.get(i))) {
                return null;
            }
            object = object.getJSONObject(fields.get(i));
            if (Objects.isNull(object)) {
                return null;
            }
        }

        if (!object.containsKey(lastKey)) {
            return null;
        }
        return getValueByField(object, lastKey, clazz);
    }

    public static <T> T getValueByField(JSONObject object, String field, Class<T> clazz) {
        if (clazz == Long.class) {
            return clazz.cast(object.getLong(field));
        } else if (clazz == Integer.class) {
            return clazz.cast(object.getInteger(field));
        } else if (clazz == Double.class) {
            return clazz.cast(object.getDouble(field));
        } else if (clazz == Float.class) {
            return clazz.cast(object.getFloat(field));
        } else if (clazz == String.class) {
            return clazz.cast(object.getString(field));
        } else if (clazz == List.class) {
            return clazz.cast(object.getJSONArray(field));
        } else if (clazz == JSONArray.class) {
            return clazz.cast(object.getJSONArray(field));
        }
        return clazz.cast(object.getString(field));
    }

    public static <T> List<T> getListByFields(JSONObject object, List<String> fields, Class<T> clazz) {
        JSONArray array = getValueByFields(object, fields, JSONArray.class);
        if (CollectionUtils.isEmpty(array)) {
            return Collections.emptyList();
        }
        return array.toJavaList(clazz);
    }

    public static <T> T getNumberByT(String str, Class<T> clazz) {
        if (StringUtils.isEmpty(str)) {
            str = "0";
        }
        if (clazz == Integer.class) {
            return clazz.cast(Integer.parseInt(str));
        } else if (clazz == Long.class) {
            return clazz.cast(Long.parseLong(str));
        } else if (clazz == Double.class) {
            return clazz.cast(Double.parseDouble(str));
        } else if (clazz == Float.class) {
            return clazz.cast(Float.parseFloat(str));
        }
        return null;
    }

    public static <T> List<T>  objectToList(Object object, Class<T> clazz) {
        List<?> rawList = (List<?>) object;
        List<T> list = new ArrayList<>();
        for (Object item : rawList) {
            if (item instanceof String) {
                list.add(clazz.cast(String.valueOf(item)));
            } else {
                // 处理非 String 元素（记录日志、抛异常或跳过）
            }
        }
        return list;
    }

}