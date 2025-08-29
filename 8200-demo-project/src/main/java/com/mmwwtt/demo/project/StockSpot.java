package com.mmwwtt.demo.project;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class StockSpot {

    private static final OkHttpClient HTTP = new OkHttpClient();
    private static final int BATCH = 600;   // 新浪单批上限

    public static void main(String[] args) throws IOException {
        // 1. 获取沪深京全部股票代码（这里用新浪静态列表，也可换成自己库）
        List<String> allCodes = fetchAllCodes();

        // 2. 分批次拉取实时快照
        List<Spot> list = new ArrayList<>();
        for (int i = 0; i < allCodes.size(); i += BATCH) {
            List<String> batch = allCodes.subList(i, Math.min(i + BATCH, allCodes.size()));
            list.addAll(fetchBatch(batch));
        }

        // 3. 过滤 / 排序 / 打印
        list.stream()
                .filter(s -> s.amount > 10000)   // 成交额 > 1 亿元
                .sorted(Comparator.comparing(s -> -s.pctChange))
                .limit(10)
                .forEach(System.out::println);
    }

    /* ---------------------------------- 工具方法 ---------------------------------- */

    // 从新浪静态文件拿全部沪深京代码（每天更新）
    private static List<String> fetchAllCodes() throws IOException {
        String url = "http://file.tushare.org/static/sina_all.txt";
        Request req = new Request.Builder().url(url).build();
        try (Response resp = HTTP.newCall(req).execute()) {
            String body = resp.body().string();
            return Arrays.stream(body.split("\n"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
    }

    // 拉取一批代码的实时行情
    private static List<Spot> fetchBatch(List<String> codes) throws IOException {
        String codeParam = codes.stream()
                .map(c -> c.startsWith("6") ? "sh" + c :
                        (c.startsWith("4") || c.startsWith("8") ? "bj" + c : "sz" + c))
                .collect(Collectors.joining(","));
        String url = "https://hq.sinajs.cn/list=" + codeParam;
        Request req = new Request.Builder().url(url).build();
        try (Response resp = HTTP.newCall(req).execute()) {
            String body = resp.body().string();
            return parse(body);
        }
    }

    // 解析新浪返回格式
    private static List<Spot> parse(String body) {
        List<Spot> list = new ArrayList<>();
        String[] lines = body.split("\n");
        for (String line : lines) {
            if (!line.startsWith("var hq_str_")) continue;
            int eq = line.indexOf('=');
            String code = line.substring("var hq_str_".length(), eq);
            String data = line.substring(eq + 2, line.length() - 2); // 去掉引号
            if (data.isEmpty()) continue;
            String[] arr = data.split(",");
            Spot s = new Spot();
            s.code = code.substring(2);          // 去掉 sh/sz/bj
            s.name = arr[0];
            s.price = Double.parseDouble(arr[3]);
            s.pctChange = Double.parseDouble(arr[3]) / Double.parseDouble(arr[2]) * 100 - 100;
            s.amount = Double.parseDouble(arr[9]) / 10000; // 万 -> 亿
            list.add(s);
        }
        return list;
    }

    /* ---------------------------------- 数据模型 ---------------------------------- */
    public static class Spot {
        public String code;
        public String name;
        public double price;
        public double pctChange;
        public double amount;   // 成交额（亿元）

        @Override
        public String toString() {
            return String.format("%s %s %.2f %.2f%% 成交%.2f亿",
                    code, name, price, pctChange, amount);
        }
    }
}