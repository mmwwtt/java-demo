package com.mmwwtt.demo.project;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MainForceScan {

    private static final OkHttpClient HTTP = new OkHttpClient();

    public static void main(String[] args) throws Exception {
        // 1. 股票池
        List<String> symbols = Arrays.asList("sh600584", "sh688372", "sz002152",
                "sz002542", "sh600410", "bj430476");
        // 2. 回测日期（T）
        LocalDate endDate = LocalDate.of(2025, 8, 15);

        List<Match> result = new ArrayList<>();
        for (String s : symbols) {
            List<KLine> kLines = getDaily(s, endDate.minusDays(15), endDate);
            if (kLines.size() < 10) continue;
            Match m = scan(kLines);
            if (m.hit) {
                m.code = s;
                result.add(m);
            }
        }

        // 3. 打印结果
        result.forEach(r -> System.out.println(r.code + "  tailChg=" + r.tailChg +
                "  volumeRatio=" + r.volumeRatio));
    }

    /* ------------------ 核心扫描 ------------------ */
    private static Match scan(List<KLine> kLines) {
        final int N = 8;             // 吸筹天数
        final double AMP_MAX = 5;    // 振幅阈值 %
        final double MA_GAP = 2;     // 均线粘合 %
        final double TAIL_MIN = 2;   // 尾盘异动 %
        final double VR_MIN = 3;     // 量比阈值

        int len = kLines.size();
        List<KLine> base = kLines.subList(len - N - 1, len - 1); // 前 N 日
        KLine today = kLines.get(len - 1);
        KLine yest = kLines.get(len - 2);

        // 1) 缩量
        double maxVol = base.stream().mapToDouble(k -> k.vol).max().orElse(0);
        double avgVol = base.stream().mapToDouble(k -> k.vol).average().orElse(0);
        boolean shrink = avgVol < maxVol * 0.5;

        // 2) 横盘振幅
        double low = base.stream().mapToDouble(k -> k.low).min().orElse(0);
        double high = base.stream().mapToDouble(k -> k.high).max().orElse(0);
        boolean flat = (high / low - 1) * 100 < AMP_MAX;

        // 3) 均线粘合
        double ma5  = sma(kLines, 5);
        double ma10 = sma(kLines, 10);
        double ma20 = sma(kLines, 20);
        double gap = Math.max(ma5, Math.max(ma10, ma20)) /
                Math.min(ma5, Math.min(ma10, ma20)) - 1;
        boolean tight = gap * 100 < MA_GAP;

        // 4) 尾盘异动（T-1日）
        boolean tailUp = (yest.close / yest.open - 1) * 100 >= TAIL_MIN;

        // 5) 当日量比
        double avg5Vol = kLines.subList(len - 6, len - 1)
                .stream().mapToDouble(k -> k.vol).average().orElse(0);
        double volumeRatio = today.vol / avg5Vol;

        Match m = new Match();
        m.hit = shrink && flat && tight && tailUp && volumeRatio >= VR_MIN;
        m.tailChg = (yest.close / yest.open - 1) * 100;
        m.volumeRatio = volumeRatio;
        return m;
    }

    /* ------------------ 工具方法 ------------------ */
    private static double sma(List<KLine> kLines, int period) {
        int len = kLines.size();
        List<Double> closes = kLines.subList(len - period, len)
                .stream().map(k -> k.close).collect(Collectors.toList());
        return closes.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }

    /* 新浪日线接口：symbol=sh600000  sz000001  bj430476  */
    private static List<KLine> getDaily(String symbol, LocalDate start, LocalDate end) throws IOException {
        String url = "https://quotes.sina.cn/cn/api/jsonp_v2.php/var%20_" +
                symbol + "/KC_MarketDataService.getKLineData?" +
                "symbol=" + symbol + "&scale=240&ma=no&datalen=" + 200;

        Request req = new Request.Builder().url(url).build();
        try (Response resp = HTTP.newCall(req).execute()) {
            String body = resp.body().string();
            int s = body.indexOf('(') + 1;
            int e = body.lastIndexOf(')');
            body = body.substring(s, e);
            JSONArray arr = JSONArray.parse(body);
            List<KLine> list = new ArrayList<>();
            for (int i = 0; i < arr.size(); i++) {
                JSONObject o = arr.getJSONObject(i);
                KLine k = new KLine();
                k.day = o.getString("day");
                k.open = o.getDouble("open");
                k.high = o.getDouble("high");
                k.low  = o.getDouble("low");
                k.close = o.getDouble("close");
                k.vol   = o.getDouble("volume");
                if (!k.day.isEmpty()) {
                    LocalDate d = LocalDate.parse(k.day);
                    if (!d.isBefore(start) && !d.isAfter(end)) list.add(k);
                }
            }
            return list;
        }
    }

    /* ------------------ 数据模型 ------------------ */
    private static class KLine {
        String day;
        double open, high, low, close, vol;
    }

    private static class Match {
        String code;
        boolean hit;
        double tailChg, volumeRatio;
    }
}