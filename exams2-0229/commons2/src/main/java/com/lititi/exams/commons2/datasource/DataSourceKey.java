package com.lititi.exams.commons2.datasource;

public class DataSourceKey {

    private static final ThreadLocal<String> holder = new ThreadLocal<>();

    public static void setKey(String type) {
        //type检查判断留在DatabaseAOP中处理
        holder.set(type);
    }

    public static String getKey() {
        return holder.get();
    }

}
