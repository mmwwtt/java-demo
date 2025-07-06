package com.lititi.exams.commons2.utils;



import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.lititi.exams.commons2.log.LttLogger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.CollectionUtils;

public class LogUtil {
    private static final boolean DEBUGENABLED;
    private static final boolean INFOENABLED;

    private LogUtil() {
    }

    public static boolean isDebugEnabled() {
        return DEBUGENABLED;
    }

    public static boolean isInfoEnabled() {
        return INFOENABLED;
    }

    public static String buildTime(long start, long end) {
        return ",cost time[ms]:" + (end - start);
    }

    public static String parse(Object object, int lay, String excludeReg, HashSet<Object> linkedParent) {
        HashSet<Object> linked = null;
        if (linkedParent == null) {
            linked = new HashSet<>();
        } else {
            linked = new HashSet<>(linkedParent);
        }

        Class<?> clazz = object.getClass();
        if (!ClassUtil.isComplex(clazz)) {
            return object.toString();
        } else if (linked.contains(object)) {
            return CommonUtil.getPrefixBlank(lay) + "[referenced]";
        } else {
            linked.add(object);
            if (object instanceof Collection) {
                return parseCollection((Collection)object, lay, excludeReg, linked);
            } else if (object instanceof Map) {
                return parseMap((Map)object, lay, excludeReg, linked);
            } else if (clazz.isArray()) {
                Object[] arr = null;
                Class<?> cmpType = clazz.getComponentType();
                if (cmpType == Long.TYPE) {
                    arr = CommonUtil.parse2ObjectArray((long[])((long[])object));
                } else if (cmpType == Integer.TYPE) {
                    arr = CommonUtil.parse2ObjectArray((int[])((int[])object));
                } else if (cmpType == Short.TYPE) {
                    arr = CommonUtil.parse2ObjectArray((short[])((short[])object));
                } else if (cmpType == Double.TYPE) {
                    arr = CommonUtil.parse2ObjectArray((double[])((double[])object));
                } else if (cmpType == Float.TYPE) {
                    arr = CommonUtil.parse2ObjectArray((float[])((float[])object));
                } else {
                    arr = (Object[])((Object[])object);
                }

                return parseArray(arr, lay, excludeReg, linked);
            } else {
                return parseObject(object, lay, excludeReg, linked);
            }
        }
    }

    private static String parseCollection(Collection<?> coll, int lay, String excludeReg, HashSet<Object> linkedParent) {
        StringBuilder sb = new StringBuilder();
        if (coll.isEmpty()) {
            sb.append(sb.append(CommonUtil.getPrefixBlank(lay)).append("[empty]"));
        } else {
            Object[] arr = coll.toArray(new Object[coll.size()]);
            sb.append(parseArray(arr, lay, excludeReg, linkedParent));
        }

        return sb.toString();
    }

    private static String parseArray(Object[] arr, int lay, String excludeReg, HashSet<Object> linkedParent) {
        StringBuilder sb = new StringBuilder();
        String blankStr = CommonUtil.getPrefixBlank(lay);
        if (ArrayUtils.isEmpty(arr)) {
            return blankStr + "[empty]";
        } else {
            for(int i = 0; i < arr.length; ++i) {
                Object item = arr[i];
                if (sb.length() > 0) {
                    sb.append("\n");
                }

                sb.append(blankStr);
                sb.append("[").append(i).append("] : ");
                Class<?> type = item == null ? null : item.getClass();
                sb.append(parseItem(item, type, lay, excludeReg, linkedParent));
            }

            return sb.toString();
        }
    }

    private static String parseMap(Map<?, ?> map, int lay, String excludeReg, HashSet<Object> linkedParent) {
        StringBuilder sb = new StringBuilder();
        String blankStr = CommonUtil.getPrefixBlank(lay);
        if (CollectionUtils.isEmpty(map)) {
            return blankStr + "[empty]";
        } else {

            for (Entry<?, ?> entry : map.entrySet()) {
                Object key = entry.getKey();
                Object value = entry.getValue();
                if (sb.length() > 0) {
                    sb.append("\n");
                }

                sb.append(blankStr);
                sb.append("[key]").append(key).append(" : ");
                Class<?> type = value == null ? null : value.getClass();
                sb.append(parseItem(value, type, lay, excludeReg, linkedParent));
            }

            return sb.toString();
        }
    }

    private static String parseObject(Object object, int lay, String excludeReg, HashSet<Object> linkedParent) {
        Field[] fs = ClassUtil.getFields(object.getClass());
        if (!ArrayUtils.isEmpty(fs) && !(object instanceof LttLogger)) {
            BeanWrapperImpl beanWarpper = new BeanWrapperImpl(object);
            StringBuilder sb = new StringBuilder();

            for (Field f : fs) {
                String fName = f.getName();
                if (!fName.startsWith("$") && (StringUtils.isEmpty(excludeReg) || !Pattern.matches(excludeReg, fName))
                    && !"serialVersionUID".equals(fName)) {
                    Class<?> fType = f.getType();
                    try {
                        Object fValue = beanWarpper.getPropertyValue(fName);
                        if (sb.length() > 0) {
                            sb.append("\n");
                        }
                        sb.append(CommonUtil.getPrefixBlank(lay)).append(fName).append(" : ");
                        sb.append(parseItem(fValue, fType, lay, excludeReg, linkedParent));
                    } catch (Exception var14) {
                        (new LttLogger(LogUtil.class)).error(var14);
                    }
                }
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    private static String parseItem(Object value, Class<?> type, int lay, String excludeReg, HashSet<Object> linkedParent) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbType = new StringBuilder();
        if (type != null) {
            String typeName = type.isArray() ? type.getComponentType().getName() + "[]" : type.getName();
            sbType.append("                   ").append(typeName);
        }

        if (value == null) {
            sb.append("[null]");
            sb.append(sbType);
        } else if (!ClassUtil.isComplex(value.getClass())) {
            if (value instanceof Date) {
                value = DateUtil.getFormatDate((Date)value, "yyyy-MM-dd HH:mm:ss");
            }

            sb.append(value.toString());
            sb.append(sbType);
        } else {
            sb.append(sbType).append("@").append(value.hashCode())
                .append("\n").append(parse(value, lay + 1, excludeReg, linkedParent));
        }

        return sb.toString();
    }

    static {
        Logger logger = LoggerFactory.getLogger(LogUtil.class);
        DEBUGENABLED = logger.isDebugEnabled();
        INFOENABLED = logger.isInfoEnabled();
    }
}

