package com.mmwwtt.stock.service;

import com.mmwwtt.stock.entity.StockDetail;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class StockGuiUitls {

    private static final int IMG_W = 600;   // 图片宽
    private static final int IMG_H = 500;   // 图片高
    private static final int MARGIN = 40;   // 四边留白
    private static final int GAP    = 10;   // K 线区与量区间距

    public static void genDetailImage(StockDetail t0, String path) throws IOException {
        List<StockDetail> list = new ArrayList<>();
        list.add(t0.getNext10());
        list.add(t0.getNext5());
        list.add(t0.getNext4());
        list.add(t0.getNext3());
        list.add(t0.getNext2());
        list.add(t0.getNext1());
        list.add(t0);
        list.add(t0.getT1());
        list.add(t0.getT2());
        list.add(t0.getT3());
        list.add(t0.getT4());
        list.add(t0.getT5());
        list.removeIf(Objects::isNull);
        Collections.reverse(list);
        /* 2. 绘图并保存 */
        BufferedImage img = draw(list);
        String imageName = String.format("%s_%s.png", t0.getStockCode(),t0.getDealDate());
        String filePath = "D:\\1.moweitao\\test\\" + path + "\\";
        filePath = filePath.replace("%", "");
        filePath = filePath.replace("<", "");
        filePath = filePath.replace(">", "");
        Files.createDirectories(Path.of(filePath));
        File file = new File(filePath + imageName);
        ImageIO.write(img, "PNG", file);
    }

    private static BufferedImage draw(List<StockDetail> list) {
        BufferedImage img = new BufferedImage(IMG_W, IMG_H, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        /* 背景 */
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, IMG_W, IMG_H);

        /* 上下分区 */
        double priceH = (IMG_H - 2 * MARGIN - GAP) * 0.75;
        double volH   = (IMG_H - 2 * MARGIN - GAP) * 0.25;

        /* 最大最小 */
        double maxPrice = list.stream().mapToDouble(d -> d.getHighPrice().doubleValue()).max().orElse(0);
        double minPrice = list.stream().mapToDouble(d -> d.getLowPrice().doubleValue()).min().orElse(0);
        double maxVol   = list.stream().mapToDouble(d -> d.getDealQuantity().doubleValue()).max().orElse(0);
        double priceRange = maxPrice - minPrice;
        double candleW = (IMG_W - 2 * MARGIN) / (double) list.size();

        /* 边框 */
        g2.setColor(Color.GRAY);
        g2.drawRect(MARGIN, MARGIN, IMG_W - 2 * MARGIN, (int) priceH);
        g2.drawRect(MARGIN, (int) (MARGIN + priceH + GAP), IMG_W - 2 * MARGIN, (int) volH);

        for (int i = 0; i < list.size(); i++) {
            StockDetail d = list.get(i);
            double x = MARGIN + i * candleW + candleW / 2;

            double open  = d.getStartPrice().doubleValue();
            double close = d.getEndPrice().doubleValue();
            double high  = d.getHighPrice().doubleValue();
            double low   = d.getLowPrice().doubleValue();
            double vol   = d.getDealQuantity().doubleValue();
            boolean up   = close >= open;

            Color color = up ?   Color.decode("#e6c7c7") : Color.decode("#c8e0c8");

            /* 坐标映射 */
            double highY = MARGIN + (maxPrice - high) / priceRange * priceH;
            double lowY  = MARGIN + (maxPrice - low)  / priceRange * priceH;
            double openY = MARGIN + (maxPrice - open) / priceRange * priceH;
            double closeY= MARGIN + (maxPrice - close)/ priceRange * priceH;

            /* 影线 */
            g2.setColor(color);
            g2.drawLine((int) x, (int) highY, (int) x, (int) lowY);

            /* 实体 */
            double bodyTop = Math.min(openY, closeY);
            double bodyH   = Math.abs(openY - closeY);
            if (bodyH < 1) bodyH = 1;
            g2.fillRect((int) (x - candleW * 0.3), (int) bodyTop, (int) (candleW * 0.6), (int) bodyH);

            /* 成交量柱 */
            double volY = MARGIN + priceH + GAP + (1 - vol / maxVol) * volH;
            double volBarH = vol / maxVol * volH;
            g2.fillRect((int) (x - candleW * 0.3), (int) volY, (int) (candleW * 0.6), (int) volBarH);

            /* 日期 */
            g2.setColor(Color.BLACK);
            try {
                g2.drawString(d.getDealDate().substring(4,8), (int) (x - 20), IMG_H - 10);
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
            g2.drawString(d.getDealDate().substring(4,8), (int) (x - 20), IMG_H - 10);
        }

        g2.dispose();
        return img;
    }
}
