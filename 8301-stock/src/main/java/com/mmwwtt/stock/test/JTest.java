package com.mmwwtt.stock.test;

import com.mmwwtt.stock.common.GlobalThreadPool;
import com.mmwwtt.stock.entity.StockDetail;
import com.mmwwtt.stock.entity.StrategyEnum;
import com.mmwwtt.stock.entity.StrategyWin;
import com.mmwwtt.stock.service.impl.CalcCommonService;
import com.mmwwtt.stock.service.impl.CommonService;
import com.mmwwtt.stock.service.impl.StrategyWinServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Function;

import static com.mmwwtt.stock.common.CommonUtils.*;

@SpringBootTest
@Slf4j
public class JTest {

    @Resource
    private StrategyWinServiceImpl strategyWinService;

    @Resource
    private CommonService commonService;

    @Resource
    private CalcCommonService calcCommonService;

    private static List<StrategyWin> winList;

    private final ThreadPoolExecutor ioThreadPool = GlobalThreadPool.getIoThreadPool();

    @Test
    @DisplayName("测试单个策略-自定义")
    public void startCalc4() throws ExecutionException, InterruptedException {
        // 日本蜡烛图形态策略 - 全部经典形态
        List<StrategyEnum> strategyEnums = buildCandlestickStrategyEnums();
        calcByStrategy(strategyEnums);
    }

    /**
     * 将形态策略按位置（40日区间 position40）拆分为五个变体
     * 极低位: position40 < 0.2 | 低位: 0.2~0.4 | 中位: 0.4~0.6 | 高位: 0.6~0.8 | 极高位: position40 >= 0.8
     */
    private void addWithPositionVariants(List<StrategyEnum> list, String code, String name, Function<StockDetail, Boolean> baseFunc) {
        list.add(new StrategyEnum(code + "_LL", name + "_极低位", d ->
                Boolean.TRUE.equals(baseFunc.apply(d)) && d.getPosition40() != null && lessThan(d.getPosition40(), "0.2")));
        list.add(new StrategyEnum(code + "_L", name + "_低位", d ->
                Boolean.TRUE.equals(baseFunc.apply(d)) && d.getPosition40() != null && isInRange(d.getPosition40(), "0.2", "0.4")));
        list.add(new StrategyEnum(code + "_M", name + "_中位", d ->
                Boolean.TRUE.equals(baseFunc.apply(d)) && d.getPosition40() != null && isInRange(d.getPosition40(), "0.4", "0.6")));
        list.add(new StrategyEnum(code + "_H", name + "_高位", d ->
                Boolean.TRUE.equals(baseFunc.apply(d)) && d.getPosition40() != null && isInRange(d.getPosition40(), "0.6", "0.8")));
        list.add(new StrategyEnum(code + "_HH", name + "_极高位", d ->
                Boolean.TRUE.equals(baseFunc.apply(d)) && d.getPosition40() != null && moreThan(d.getPosition40(), "0.8")));
    }

    /** 构建所有日本蜡烛图形态策略（125种形态 × 五档位置 = 625种） */
    private List<StrategyEnum> buildCandlestickStrategyEnums() {
        List<StrategyEnum> list = new ArrayList<>();

        // ========== 单根K线形态 ==========
        addWithPositionVariants(list, "C01", "十字星_Doji", StockDetail::getIsTenStar);

        addWithPositionVariants(list, "C02", "锤子线_Hammer", (StockDetail d) -> {
            if (d.getLowShadowLen() == null || d.getEntityLen() == null || d.getUpShadowLen() == null) return false;
            return moreThan(d.getLowShadowLen(), multiply(d.getEntityLen(), "2"))
                    && lessThan(d.getUpShadowLen(), "0.02")
                    && moreThan(d.getEntityLen(), "0");
        });

        addWithPositionVariants(list, "C03", "倒锤子_InvertedHammer", (StockDetail d) -> {
            if (d.getUpShadowLen() == null || d.getEntityLen() == null || d.getLowShadowLen() == null) return false;
            return moreThan(d.getUpShadowLen(), multiply(d.getEntityLen(), "2"))
                    && lessThan(d.getLowShadowLen(), "0.02")
                    && moreThan(d.getEntityLen(), "0");
        });

        addWithPositionVariants(list, "C04", "上吊线_HangingMan", (StockDetail d) -> {
            if (d.getLowShadowLen() == null || d.getEntityLen() == null || d.getUpShadowLen() == null) return false;
            return moreThan(d.getLowShadowLen(), multiply(d.getEntityLen(), "2"))
                    && lessThan(d.getUpShadowLen(), "0.02")
                    && Boolean.TRUE.equals(d.getIsGreen());
        });

        addWithPositionVariants(list, "C05", "射击之星_ShootingStar", (StockDetail d) -> {
            if (d.getUpShadowLen() == null || d.getEntityLen() == null || d.getLowShadowLen() == null) return false;
            return moreThan(d.getUpShadowLen(), multiply(d.getEntityLen(), "2"))
                    && lessThan(d.getLowShadowLen(), "0.02")
                    && Boolean.TRUE.equals(d.getIsGreen());
        });

        addWithPositionVariants(list, "C06", "纺锤线_SpinningTop", (StockDetail d) -> {
            if (d.getEntityLen() == null || d.getAllLen() == null || d.getUpShadowLen() == null || d.getLowShadowLen() == null) return false;
            return isInRangeNotEquals(d.getEntityPert(), "0.1", "0.4")
                    && moreThan(d.getUpShadowLen(), "0.02")
                    && moreThan(d.getLowShadowLen(), "0.02");
        });

        addWithPositionVariants(list, "C07", "长实体_Marubozu", (StockDetail d) -> {
            if (d.getEntityPert() == null || d.getUpShadowPert() == null || d.getLowShadowPert() == null) return false;
            return moreThan(d.getEntityPert(), "0.85")
                    && lessThan(d.getUpShadowPert(), "0.1")
                    && lessThan(d.getLowShadowPert(), "0.1");
        });

        addWithPositionVariants(list, "C08", "蜻蜓十字_DragonflyDoji", (StockDetail d) -> {
            if (d.getLowShadowLen() == null || d.getUpShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsTenStar())
                    && moreThan(d.getLowShadowLen(), "0.03")
                    && lessThan(d.getUpShadowLen(), "0.005");
        });

        addWithPositionVariants(list, "C09", "墓碑十字_GravestoneDoji", (StockDetail d) -> {
            if (d.getUpShadowLen() == null || d.getLowShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsTenStar())
                    && moreThan(d.getUpShadowLen(), "0.03")
                    && lessThan(d.getLowShadowLen(), "0.005");
        });

        // ========== 双K线形态 ==========
        addWithPositionVariants(list, "C10", "看涨吞没_BullishEngulfing", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(t1.getIsGreen())
                    && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), t1.getStartPrice());
        });

        addWithPositionVariants(list, "C11", "看跌吞没_BearishEngulfing", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(d.getIsGreen()) && Boolean.TRUE.equals(t1.getIsRed())
                    && moreThan(d.getStartPrice(), t1.getEndPrice())
                    && lessThan(d.getEndPrice(), t1.getStartPrice());
        });

        addWithPositionVariants(list, "C12", "看涨孕线_BullishHarami", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getEndPrice(), t1.getStartPrice())
                    && moreThan(d.getStartPrice(), t1.getEndPrice());
        });

        addWithPositionVariants(list, "C13", "看跌孕线_BearishHarami", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsRed()) && Boolean.TRUE.equals(d.getIsGreen())
                    && moreThan(d.getEndPrice(), t1.getStartPrice())
                    && lessThan(d.getStartPrice(), t1.getEndPrice());
        });

        addWithPositionVariants(list, "C14", "刺透形态_PiercingLine", (StockDetail d) -> {
            StockDetail t1 = d.getT1();
            if (t1 == null || t1.getStartPrice() == null || t1.getEndPrice() == null) return false;
            BigDecimal sum = add(t1.getStartPrice(), t1.getEndPrice());
            if (sum == null) return false;
            BigDecimal mid = sum.divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), mid)
                    && lessThan(d.getEndPrice(), t1.getStartPrice());
        });

        addWithPositionVariants(list, "C15", "乌云盖顶_DarkCloudCover", (StockDetail d) -> {
            StockDetail t1 = d.getT1();
            if (t1 == null || t1.getStartPrice() == null || t1.getEndPrice() == null) return false;
            BigDecimal sum = add(t1.getStartPrice(), t1.getEndPrice());
            if (sum == null) return false;
            BigDecimal mid = sum.divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
            return Boolean.TRUE.equals(t1.getIsRed()) && Boolean.TRUE.equals(d.getIsGreen())
                    && moreThan(d.getStartPrice(), t1.getEndPrice())
                    && lessThan(d.getEndPrice(), mid)
                    && moreThan(d.getEndPrice(), t1.getStartPrice());
        });

        addWithPositionVariants(list, "C16", "双顶_TweezerTops", (StockDetail d) -> {
            if (d.getT1() == null || d.getHighPrice() == null || d.getT1().getHighPrice() == null || d.getLastPrice() == null) return false;
            BigDecimal sub = subtract(d.getHighPrice(), d.getT1().getHighPrice());
            BigDecimal diff = sub != null ? sub.abs() : null;
            return diff != null && lessThan(divide(diff, d.getLastPrice()), "0.01");
        });

        addWithPositionVariants(list, "C17", "双底_TweezerBottoms", (StockDetail d) -> {
            if (d.getT1() == null || d.getLowPrice() == null || d.getT1().getLowPrice() == null || d.getLastPrice() == null) return false;
            BigDecimal sub = subtract(d.getLowPrice(), d.getT1().getLowPrice());
            BigDecimal diff = sub != null ? sub.abs() : null;
            return diff != null && lessThan(divide(diff, d.getLastPrice()), "0.01");
        });

        // ========== 三K线形态 ==========
        addWithPositionVariants(list, "C18", "启明星_MorningStar", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            BigDecimal t2Mid = add(t2.getStartPrice(), t2.getEndPrice());
            if (t2Mid == null) return false;
            t2Mid = t2Mid.divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
            return Boolean.TRUE.equals(t2.getIsGreen())
                    && (Boolean.TRUE.equals(t1.getIsTenStar()) || (t1.getEntityLen() != null && lessThan(t1.getEntityLen(), "0.02")))
                    && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(t1.getEndPrice(), t2.getEndPrice())
                    && moreThan(d.getEndPrice(), t2Mid);
        });

        addWithPositionVariants(list, "C19", "黄昏星_EveningStar", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            BigDecimal t2Mid = add(t2.getStartPrice(), t2.getEndPrice());
            if (t2Mid == null) return false;
            t2Mid = t2Mid.divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
            return Boolean.TRUE.equals(t2.getIsRed())
                    && (Boolean.TRUE.equals(t1.getIsTenStar()) || (t1.getEntityLen() != null && lessThan(t1.getEntityLen(), "0.02")))
                    && Boolean.TRUE.equals(d.getIsGreen())
                    && moreThan(t1.getStartPrice(), t2.getStartPrice())
                    && lessThan(d.getEndPrice(), t2Mid);
        });

        addWithPositionVariants(list, "C20", "红三兵_ThreeWhiteSoldiers", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed())
                    && moreThan(d.getEndPrice(), d.getT1().getEndPrice())
                    && moreThan(d.getT1().getEndPrice(), d.getT2().getEndPrice())
                    && moreThan(d.getStartPrice(), d.getT1().getStartPrice())
                    && moreThan(d.getT1().getStartPrice(), d.getT2().getStartPrice());
        });

        addWithPositionVariants(list, "C21", "三只乌鸦_ThreeBlackCrows", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            return Boolean.TRUE.equals(d.getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen()) && Boolean.TRUE.equals(d.getT2().getIsGreen())
                    && lessThan(d.getEndPrice(), d.getT1().getEndPrice())
                    && lessThan(d.getT1().getEndPrice(), d.getT2().getEndPrice())
                    && lessThan(d.getStartPrice(), d.getT1().getStartPrice())
                    && lessThan(d.getT1().getStartPrice(), d.getT2().getStartPrice());
        });

        addWithPositionVariants(list, "C22", "向上三法_ThreeInsideUp", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2(), t3 = d.getT3();
            return Boolean.TRUE.equals(t3.getIsGreen())
                    && Boolean.TRUE.equals(t2.getIsRed()) && Boolean.TRUE.equals(t1.getIsRed())
                    && moreThan(d.getEndPrice(), t3.getHighPrice())
                    && moreThan(t2.getLowPrice(), t3.getLowPrice()) && lessThan(t2.getHighPrice(), t3.getHighPrice())
                    && moreThan(t1.getEndPrice(), t2.getEndPrice());
        });

        addWithPositionVariants(list, "C23", "向下三法_ThreeInsideDown", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2(), t3 = d.getT3();
            return Boolean.TRUE.equals(t3.getIsRed())
                    && Boolean.TRUE.equals(t2.getIsGreen()) && Boolean.TRUE.equals(t1.getIsGreen())
                    && lessThan(d.getEndPrice(), t3.getLowPrice())
                    && lessThan(t2.getHighPrice(), t3.getHighPrice()) && moreThan(t2.getLowPrice(), t3.getLowPrice())
                    && lessThan(t1.getEndPrice(), t2.getEndPrice());
        });

        addWithPositionVariants(list, "C24", "上升三法_RisingThreeMethods", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null || d.getT4() == null) return false;
            StockDetail t4 = d.getT4();
            return Boolean.TRUE.equals(t4.getIsRed())
                    && Boolean.TRUE.equals(d.getT3().getIsGreen()) && Boolean.TRUE.equals(d.getT2().getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen())
                    && moreThan(d.getEndPrice(), t4.getHighPrice())
                    && moreThan(d.getLowPrice(), t4.getLowPrice());
        });

        addWithPositionVariants(list, "C25", "下降三法_FallingThreeMethods", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null || d.getT4() == null) return false;
            StockDetail t4 = d.getT4();
            return Boolean.TRUE.equals(t4.getIsGreen())
                    && Boolean.TRUE.equals(d.getT3().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed())
                    && lessThan(d.getEndPrice(), t4.getLowPrice())
                    && lessThan(d.getHighPrice(), t4.getHighPrice());
        });

        // ========== 多K线组合 ==========
        addWithPositionVariants(list, "C26", "二连红", (StockDetail d) -> d.getT1() != null && Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed()));
        addWithPositionVariants(list, "C27", "三连红", (StockDetail d) -> d.getT1() != null && d.getT2() != null
                && Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed()));
        addWithPositionVariants(list, "C28", "四连红", (StockDetail d) -> d.getT1() != null && d.getT2() != null && d.getT3() != null
                && Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed())
                && Boolean.TRUE.equals(d.getT2().getIsRed()) && Boolean.TRUE.equals(d.getT3().getIsRed()));
        addWithPositionVariants(list, "C29", "五连红", (StockDetail d) -> d.getT1() != null && d.getT2() != null && d.getT3() != null && d.getT4() != null
                && Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed())
                && Boolean.TRUE.equals(d.getT3().getIsRed()) && Boolean.TRUE.equals(d.getT4().getIsRed()));

        addWithPositionVariants(list, "C30", "二连阴", (StockDetail d) -> d.getT1() != null && Boolean.TRUE.equals(d.getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen()));
        addWithPositionVariants(list, "C31", "三连阴", (StockDetail d) -> d.getT1() != null && d.getT2() != null
                && Boolean.TRUE.equals(d.getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen()) && Boolean.TRUE.equals(d.getT2().getIsGreen()));
        addWithPositionVariants(list, "C32", "四连阴", (StockDetail d) -> d.getT1() != null && d.getT2() != null && d.getT3() != null
                && Boolean.TRUE.equals(d.getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen())
                && Boolean.TRUE.equals(d.getT2().getIsGreen()) && Boolean.TRUE.equals(d.getT3().getIsGreen()));
        addWithPositionVariants(list, "C33", "五连阴", (StockDetail d) -> d.getT1() != null && d.getT2() != null && d.getT3() != null && d.getT4() != null
                && Boolean.TRUE.equals(d.getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen()) && Boolean.TRUE.equals(d.getT2().getIsGreen())
                && Boolean.TRUE.equals(d.getT3().getIsGreen()) && Boolean.TRUE.equals(d.getT4().getIsGreen()));

        // ========== 补充单根K线 ==========
        addWithPositionVariants(list, "C34", "长腿十字_LongLeggedDoji", (StockDetail d) -> {
            if (d.getUpShadowLen() == null || d.getLowShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsTenStar())
                    && moreThan(d.getUpShadowLen(), "0.03")
                    && moreThan(d.getLowShadowLen(), "0.03");
        });
        addWithPositionVariants(list, "C35", "大阳线_BigWhiteCandle", (StockDetail d) -> {
            if (d.getEntityPert() == null || d.getUpShadowPert() == null || d.getLowShadowPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed())
                    && moreThan(d.getEntityPert(), "0.7")
                    && lessThan(d.getUpShadowPert(), "0.15")
                    && lessThan(d.getLowShadowPert(), "0.15");
        });
        addWithPositionVariants(list, "C36", "大阴线_BigBlackCandle", (StockDetail d) -> {
            if (d.getEntityPert() == null || d.getUpShadowPert() == null || d.getLowShadowPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsGreen())
                    && moreThan(d.getEntityPert(), "0.7")
                    && lessThan(d.getUpShadowPert(), "0.15")
                    && lessThan(d.getLowShadowPert(), "0.15");
        });
        addWithPositionVariants(list, "C37", "长上影线_LongUpperShadow", (StockDetail d) -> {
            if (d.getUpShadowPert() == null) return false;
            return moreThan(d.getUpShadowPert(), "0.6");
        });
        addWithPositionVariants(list, "C38", "长下影线_LongLowerShadow", (StockDetail d) -> {
            if (d.getLowShadowPert() == null) return false;
            return moreThan(d.getLowShadowPert(), "0.6");
        });
        addWithPositionVariants(list, "C39", "光头阳线_ShavenHeadRed", (StockDetail d) -> {
            if (d.getUpShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && lessThan(d.getUpShadowLen(), "0.005");
        });
        addWithPositionVariants(list, "C40", "光脚阳线_ShavenBottomRed", (StockDetail d) -> {
            if (d.getLowShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && lessThan(d.getLowShadowLen(), "0.005");
        });
        addWithPositionVariants(list, "C41", "光头阴线_ShavenHeadGreen", (StockDetail d) -> {
            if (d.getUpShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsGreen()) && lessThan(d.getUpShadowLen(), "0.005");
        });
        addWithPositionVariants(list, "C42", "光脚阴线_ShavenBottomGreen", (StockDetail d) -> {
            if (d.getLowShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsGreen()) && lessThan(d.getLowShadowLen(), "0.005");
        });
        addWithPositionVariants(list, "C43", "高浪线_HighWaveCandle", (StockDetail d) -> {
            if (d.getEntityPert() == null || d.getUpShadowLen() == null || d.getLowShadowLen() == null) return false;
            return lessThan(d.getEntityPert(), "0.3")
                    && moreThan(d.getUpShadowLen(), "0.04")
                    && moreThan(d.getLowShadowLen(), "0.04");
        });

        // ========== 补充双K线 ==========
        addWithPositionVariants(list, "C44", "看涨孕线十字_BullishHaramiCross", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsTenStar())
                    && lessThan(d.getHighPrice(), t1.getStartPrice())
                    && moreThan(d.getLowPrice(), t1.getEndPrice());
        });
        addWithPositionVariants(list, "C45", "看跌孕线十字_BearishHaramiCross", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsRed()) && Boolean.TRUE.equals(d.getIsTenStar())
                    && moreThan(d.getLowPrice(), t1.getStartPrice())
                    && lessThan(d.getHighPrice(), t1.getEndPrice());
        });
        addWithPositionVariants(list, "C46", "上升跳空_RisingWindow", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            return moreThan(d.getLowPrice(), d.getT1().getHighPrice());
        });
        addWithPositionVariants(list, "C47", "下降跳空_FallingWindow", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            return lessThan(d.getHighPrice(), d.getT1().getLowPrice());
        });
        addWithPositionVariants(list, "C48", "反击线看涨_CounterattackBullish", (StockDetail d) -> {
            if (d.getT1() == null || d.getLastPrice() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal closeDiff = subtract(d.getEndPrice(), t1.getStartPrice());
            BigDecimal relDiff = closeDiff != null && d.getLastPrice() != null ? divide(closeDiff.abs(), d.getLastPrice()) : null;
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), t1.getStartPrice())
                    && relDiff != null && lessThan(relDiff, "0.02");
        });
        addWithPositionVariants(list, "C49", "反击线看跌_CounterattackBearish", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsRed()) && Boolean.TRUE.equals(d.getIsGreen())
                    && moreThan(d.getStartPrice(), t1.getEndPrice())
                    && lessThan(d.getEndPrice(), t1.getStartPrice());
        });

        // ========== 补充三K线 ==========
        addWithPositionVariants(list, "C50", "启明十字星_MorningDojiStar", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            BigDecimal t2Mid = add(t2.getStartPrice(), t2.getEndPrice());
            if (t2Mid == null) return false;
            t2Mid = t2Mid.divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
            return Boolean.TRUE.equals(t2.getIsGreen())
                    && Boolean.TRUE.equals(t1.getIsTenStar())
                    && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(t1.getEndPrice(), t2.getEndPrice())
                    && moreThan(d.getEndPrice(), t2Mid);
        });
        addWithPositionVariants(list, "C51", "黄昏十字星_EveningDojiStar", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            BigDecimal t2Mid = add(t2.getStartPrice(), t2.getEndPrice());
            if (t2Mid == null) return false;
            t2Mid = t2Mid.divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
            return Boolean.TRUE.equals(t2.getIsRed())
                    && Boolean.TRUE.equals(t1.getIsTenStar())
                    && Boolean.TRUE.equals(d.getIsGreen())
                    && moreThan(t1.getStartPrice(), t2.getStartPrice())
                    && lessThan(d.getEndPrice(), t2Mid);
        });
        addWithPositionVariants(list, "C52", "十字星形态_DojiStar", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            return Boolean.TRUE.equals(d.getIsTenStar())
                    && (moreThan(d.getLowPrice(), d.getT1().getHighPrice()) || lessThan(d.getHighPrice(), d.getT1().getLowPrice()));
        });

        // ========== 长影线+阴阳组合 ==========
        addWithPositionVariants(list, "C53", "长下影阳线", (StockDetail d) -> {
            if (d.getLowShadowPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && moreThan(d.getLowShadowPert(), "0.5");
        });
        addWithPositionVariants(list, "C54", "长上影阳线", (StockDetail d) -> {
            if (d.getUpShadowPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && moreThan(d.getUpShadowPert(), "0.5");
        });
        addWithPositionVariants(list, "C55", "长下影阴线", (StockDetail d) -> {
            if (d.getLowShadowPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsGreen()) && moreThan(d.getLowShadowPert(), "0.5");
        });
        addWithPositionVariants(list, "C56", "长上影阴线", (StockDetail d) -> {
            if (d.getUpShadowPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsGreen()) && moreThan(d.getUpShadowPert(), "0.5");
        });

        // ========== 上升/下降跳空并列 ==========
        addWithPositionVariants(list, "C57", "上升跳空并列阳_UpsideTasuki", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            return moreThan(d.getT1().getLowPrice(), d.getT2().getHighPrice())
                    && Boolean.TRUE.equals(d.getIsRed())
                    && Boolean.TRUE.equals(d.getT1().getIsRed())
                    && lessThan(d.getStartPrice(), d.getT1().getLowPrice())
                    && moreThan(d.getEndPrice(), d.getT1().getLowPrice());
        });
        addWithPositionVariants(list, "C58", "下降跳空并列阴_DownsideTasuki", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            return lessThan(d.getT1().getHighPrice(), d.getT2().getLowPrice())
                    && Boolean.TRUE.equals(d.getIsGreen())
                    && Boolean.TRUE.equals(d.getT1().getIsGreen())
                    && moreThan(d.getStartPrice(), d.getT1().getHighPrice())
                    && lessThan(d.getEndPrice(), d.getT1().getHighPrice());
        });

        // ========== 颈线/刺透变体 ==========
        addWithPositionVariants(list, "C59", "颈线形态_OnNeck", (StockDetail d) -> {
            if (d.getT1() == null || d.getLastPrice() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal diff = subtract(d.getEndPrice(), t1.getLowPrice());
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && diff != null && lessThan(divide(diff.abs(), d.getLastPrice()), "0.005");
        });
        addWithPositionVariants(list, "C60", "刺入形态_InNeck", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal bodyLen = subtract(t1.getStartPrice(), t1.getEndPrice());
            BigDecimal closeAbove = subtract(d.getEndPrice(), t1.getEndPrice());
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), t1.getEndPrice())
                    && bodyLen != null && bodyLen.compareTo(BigDecimal.ZERO) > 0
                    && closeAbove != null && closeAbove.compareTo(BigDecimal.ZERO) > 0
                    && lessThan(closeAbove, multiply(bodyLen, "0.15"));
        });
        addWithPositionVariants(list, "C61", "刺透变体_Thrusting", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal mid = add(t1.getStartPrice(), t1.getEndPrice());
            if (mid == null) return false;
            mid = mid.divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), t1.getEndPrice())
                    && lessThan(d.getEndPrice(), mid);
        });

        // ========== 踢脚形态 ==========
        addWithPositionVariants(list, "C62", "看涨踢脚_BullishKicking", (StockDetail d) -> {
            if (d.getT1() == null || d.getEntityPert() == null || d.getT1().getEntityPert() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && moreThan(d.getLowPrice(), t1.getHighPrice())
                    && moreThan(d.getEntityPert(), "0.8") && moreThan(t1.getEntityPert(), "0.8")
                    && (d.getUpShadowLen() == null || lessThan(d.getUpShadowLen(), "0.01"))
                    && (d.getLowShadowLen() == null || lessThan(d.getLowShadowLen(), "0.01"));
        });
        addWithPositionVariants(list, "C63", "看跌踢脚_BearishKicking", (StockDetail d) -> {
            if (d.getT1() == null || d.getEntityPert() == null || d.getT1().getEntityPert() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsRed()) && Boolean.TRUE.equals(d.getIsGreen())
                    && lessThan(d.getHighPrice(), t1.getLowPrice())
                    && moreThan(d.getEntityPert(), "0.8") && moreThan(t1.getEntityPert(), "0.8");
        });

        // ========== 分离线/相遇线 ==========
        addWithPositionVariants(list, "C64", "看涨分离线_BullishSeparating", (StockDetail d) -> {
            if (d.getT1() == null || d.getLastPrice() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal diff = subtract(d.getStartPrice(), t1.getStartPrice());
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && diff != null && lessThan(divide(diff.abs(), d.getLastPrice()), "0.005")
                    && moreThan(d.getEndPrice(), t1.getEndPrice());
        });
        addWithPositionVariants(list, "C65", "看跌分离线_BearishSeparating", (StockDetail d) -> {
            if (d.getT1() == null || d.getLastPrice() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal diff = subtract(d.getStartPrice(), t1.getStartPrice());
            return Boolean.TRUE.equals(t1.getIsRed()) && Boolean.TRUE.equals(d.getIsGreen())
                    && diff != null && lessThan(divide(diff.abs(), d.getLastPrice()), "0.005")
                    && lessThan(d.getEndPrice(), t1.getEndPrice());
        });
        addWithPositionVariants(list, "C66", "看涨相遇线_BullishMeeting", (StockDetail d) -> {
            if (d.getT1() == null || d.getLastPrice() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal diff = subtract(d.getEndPrice(), t1.getEndPrice());
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && diff != null && lessThan(divide(diff.abs(), d.getLastPrice()), "0.005")
                    && lessThan(d.getStartPrice(), t1.getStartPrice());
        });
        addWithPositionVariants(list, "C67", "看跌相遇线_BearishMeeting", (StockDetail d) -> {
            if (d.getT1() == null || d.getLastPrice() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal diff = subtract(d.getEndPrice(), t1.getEndPrice());
            return Boolean.TRUE.equals(t1.getIsRed()) && Boolean.TRUE.equals(d.getIsGreen())
                    && diff != null && lessThan(divide(diff.abs(), d.getLastPrice()), "0.005")
                    && moreThan(d.getStartPrice(), t1.getStartPrice());
        });

        // ========== 三K线补充 ==========
        addWithPositionVariants(list, "C68", "两只乌鸦_TwoCrows", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return Boolean.TRUE.equals(t2.getIsRed())
                    && Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsGreen())
                    && lessThan(d.getStartPrice(), t1.getStartPrice())
                    && moreThan(d.getEndPrice(), t2.getEndPrice())
                    && lessThan(d.getEndPrice(), t2.getStartPrice());
        });
        addWithPositionVariants(list, "C69", "向上跳空两鸦_UpsideGapTwoCrows", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return moreThan(t2.getLowPrice(), t1.getHighPrice())
                    && Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsGreen())
                    && lessThan(d.getEndPrice(), t2.getStartPrice());
        });
        addWithPositionVariants(list, "C70", "外向上三法_ThreeOutsideUp", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return Boolean.TRUE.equals(t2.getIsGreen()) && Boolean.TRUE.equals(t1.getIsRed())
                    && lessThan(t1.getStartPrice(), t2.getEndPrice()) && moreThan(t1.getEndPrice(), t2.getStartPrice())
                    && Boolean.TRUE.equals(d.getIsRed()) && moreThan(d.getEndPrice(), t1.getHighPrice());
        });
        addWithPositionVariants(list, "C71", "外向下三法_ThreeOutsideDown", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return Boolean.TRUE.equals(t2.getIsRed()) && Boolean.TRUE.equals(t1.getIsGreen())
                    && moreThan(t1.getStartPrice(), t2.getEndPrice()) && lessThan(t1.getEndPrice(), t2.getStartPrice())
                    && Boolean.TRUE.equals(d.getIsGreen()) && lessThan(d.getEndPrice(), t1.getLowPrice());
        });
        addWithPositionVariants(list, "C72", "南方三星_ThreeStarsSouth", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return Boolean.TRUE.equals(t2.getIsGreen()) && Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsGreen())
                    && t2.getEntityLen() != null && t1.getEntityLen() != null && d.getEntityLen() != null
                    && moreThan(t2.getEntityLen(), t1.getEntityLen()) && moreThan(t1.getEntityLen(), d.getEntityLen())
                    && lessThan(d.getEndPrice(), t1.getEndPrice()) && lessThan(t1.getEndPrice(), t2.getEndPrice());
        });
        addWithPositionVariants(list, "C73", "独特三川底_UniqueThreeRiver", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return Boolean.TRUE.equals(t2.getIsGreen()) && Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(t1.getLowPrice(), t2.getLowPrice()) && lessThan(d.getLowPrice(), t1.getLowPrice())
                    && moreThan(d.getEndPrice(), t1.getHighPrice()) && lessThan(d.getEndPrice(), t2.getStartPrice());
        });
        addWithPositionVariants(list, "C74", "弃婴看涨_AbandonedBabyBullish", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return Boolean.TRUE.equals(t2.getIsGreen()) && Boolean.TRUE.equals(t1.getIsTenStar()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(t1.getHighPrice(), t2.getLowPrice()) && moreThan(t1.getLowPrice(), d.getHighPrice())
                    && moreThan(d.getLowPrice(), t1.getHighPrice());
        });
        addWithPositionVariants(list, "C75", "弃婴看跌_AbandonedBabyBearish", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return Boolean.TRUE.equals(t2.getIsRed()) && Boolean.TRUE.equals(t1.getIsTenStar()) && Boolean.TRUE.equals(d.getIsGreen())
                    && moreThan(t1.getLowPrice(), t2.getHighPrice()) && lessThan(t1.getHighPrice(), d.getLowPrice())
                    && lessThan(d.getHighPrice(), t1.getLowPrice());
        });

        // ========== 四/五K线 ==========
        addWithPositionVariants(list, "C76", "看涨三线打击_BullishThreeLineStrike", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null) return false;
            return Boolean.TRUE.equals(d.getT3().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed())
                    && moreThan(d.getT1().getEndPrice(), d.getT2().getEndPrice()) && moreThan(d.getT2().getEndPrice(), d.getT3().getEndPrice())
                    && Boolean.TRUE.equals(d.getIsGreen())
                    && lessThan(d.getStartPrice(), d.getT1().getEndPrice()) && moreThan(d.getEndPrice(), d.getT3().getStartPrice());
        });
        addWithPositionVariants(list, "C77", "看跌三线打击_BearishThreeLineStrike", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null) return false;
            return Boolean.TRUE.equals(d.getT3().getIsGreen()) && Boolean.TRUE.equals(d.getT2().getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen())
                    && lessThan(d.getT1().getEndPrice(), d.getT2().getEndPrice()) && lessThan(d.getT2().getEndPrice(), d.getT3().getEndPrice())
                    && Boolean.TRUE.equals(d.getIsRed())
                    && moreThan(d.getStartPrice(), d.getT1().getEndPrice()) && lessThan(d.getEndPrice(), d.getT3().getStartPrice());
        });
        addWithPositionVariants(list, "C78", "梯底_LadderBottom", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null || d.getT4() == null) return false;
            return Boolean.TRUE.equals(d.getT4().getIsGreen()) && Boolean.TRUE.equals(d.getT3().getIsGreen()) && Boolean.TRUE.equals(d.getT2().getIsGreen())
                    && lessThan(d.getT3().getEndPrice(), d.getT4().getEndPrice()) && lessThan(d.getT2().getEndPrice(), d.getT3().getEndPrice())
                    && (Boolean.TRUE.equals(d.getT1().getIsTenStar()) || (d.getT1().getEntityLen() != null && lessThan(d.getT1().getEntityLen(), "0.02")))
                    && Boolean.TRUE.equals(d.getIsRed()) && moreThan(d.getEndPrice(), d.getT4().getHighPrice());
        });
        addWithPositionVariants(list, "C79", "藏婴吞没_ConcealingBabySwallow", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2(), t3 = d.getT3();
            return Boolean.TRUE.equals(t3.getIsGreen()) && Boolean.TRUE.equals(t2.getIsGreen()) && Boolean.TRUE.equals(t1.getIsGreen())
                    && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getStartPrice(), t1.getEndPrice()) && moreThan(d.getEndPrice(), t3.getStartPrice());
        });

        // ========== 补充基础形态 ==========
        addWithPositionVariants(list, "C80", "短阳线_ShortWhite", (StockDetail d) -> {
            if (d.getEntityPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && isInRangeNotEquals(d.getEntityPert(), "0.1", "0.35");
        });
        addWithPositionVariants(list, "C81", "短阴线_ShortBlack", (StockDetail d) -> {
            if (d.getEntityPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsGreen()) && isInRangeNotEquals(d.getEntityPert(), "0.1", "0.35");
        });
        addWithPositionVariants(list, "C82", "纺锤阳线_WhiteSpinningTop", (StockDetail d) -> {
            if (d.getEntityPert() == null || d.getUpShadowLen() == null || d.getLowShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && isInRangeNotEquals(d.getEntityPert(), "0.1", "0.4")
                    && moreThan(d.getUpShadowLen(), "0.02") && moreThan(d.getLowShadowLen(), "0.02");
        });
        addWithPositionVariants(list, "C83", "纺锤阴线_BlackSpinningTop", (StockDetail d) -> {
            if (d.getEntityPert() == null || d.getUpShadowLen() == null || d.getLowShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsGreen()) && isInRangeNotEquals(d.getEntityPert(), "0.1", "0.4")
                    && moreThan(d.getUpShadowLen(), "0.02") && moreThan(d.getLowShadowLen(), "0.02");
        });
        addWithPositionVariants(list, "C84", "两阴跳空_TwoBlackGapping", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            return Boolean.TRUE.equals(d.getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen())
                    && lessThan(d.getHighPrice(), d.getT1().getLowPrice());
        });
        addWithPositionVariants(list, "C85", "家鸽_HomingPigeon", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsGreen())
                    && moreThan(d.getStartPrice(), t1.getStartPrice()) && lessThan(d.getEndPrice(), t1.getEndPrice())
                    && moreThan(d.getLowPrice(), t1.getLowPrice()) && lessThan(d.getHighPrice(), t1.getHighPrice());
        });

        // ========== 看涨形态补充（会上涨信号） ==========
        addWithPositionVariants(list, "C86", "阳线锤子_看涨Hammer", (StockDetail d) -> {
            if (d.getLowShadowLen() == null || d.getEntityLen() == null || d.getUpShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed())
                    && moreThan(d.getLowShadowLen(), multiply(d.getEntityLen(), "2"))
                    && lessThan(d.getUpShadowLen(), "0.02")
                    && moreThan(d.getEntityLen(), "0");
        });
        addWithPositionVariants(list, "C87", "阳线倒锤子_看涨InvertedHammer", (StockDetail d) -> {
            if (d.getUpShadowLen() == null || d.getEntityLen() == null || d.getLowShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed())
                    && moreThan(d.getUpShadowLen(), multiply(d.getEntityLen(), "2"))
                    && lessThan(d.getLowShadowLen(), "0.02")
                    && moreThan(d.getEntityLen(), "0");
        });
        addWithPositionVariants(list, "C88", "探底线_TakuriLine", (StockDetail d) -> {
            if (d.getLowShadowLen() == null || d.getEntityLen() == null || d.getUpShadowLen() == null) return false;
            return moreThan(d.getLowShadowLen(), "0.05")
                    && lessThan(d.getUpShadowLen(), "0.01")
                    && (d.getEntityLen() == null || lessThan(d.getEntityLen(), "0.02"));
        });
        addWithPositionVariants(list, "C89", "光头光脚阳线_BullishBeltHold", (StockDetail d) -> {
            if (d.getLowShadowLen() == null || d.getUpShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getLowShadowLen(), "0.005")
                    && lessThan(d.getUpShadowLen(), "0.005")
                    && moreThan(d.getEntityLen(), "0.02");
        });
        addWithPositionVariants(list, "C90", "强势阳线_BullishStrongLine", (StockDetail d) -> {
            if (d.getEntityPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed())
                    && moreThan(d.getEntityPert(), "0.6")
                    && moreThan(d.getEndPrice(), d.getStartPrice());
        });
        addWithPositionVariants(list, "C91", "最后吞没底_LastEngulfingBottom", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return Boolean.TRUE.equals(t2.getIsGreen()) && Boolean.TRUE.equals(t1.getIsGreen())
                    && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), t1.getStartPrice())
                    && moreThan(d.getEndPrice(), t2.getHighPrice());
        });
        addWithPositionVariants(list, "C92", "并列阳线看涨_BullishSideBySide", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            return moreThan(d.getT1().getLowPrice(), d.getT2().getHighPrice())
                    && Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed())
                    && moreThan(d.getStartPrice(), d.getT1().getStartPrice())
                    && moreThan(d.getEndPrice(), d.getT1().getEndPrice());
        });
        addWithPositionVariants(list, "C93", "向上反转_TurnUp", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), t1.getStartPrice())
                    && moreThan(d.getEndPrice(), d.getStartPrice());
        });
        addWithPositionVariants(list, "C94", "跳空高开十字_GappingUpDoji", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            return Boolean.TRUE.equals(d.getIsTenStar())
                    && moreThan(d.getLowPrice(), d.getT1().getHighPrice());
        });
        addWithPositionVariants(list, "C95", "低位十字_SouthernDoji", (StockDetail d) -> {
            if (d.getT1() == null || d.getPosition20() == null) return false;
            return Boolean.TRUE.equals(d.getIsTenStar())
                    && lessThan(d.getPosition20(), "0.3");
        });
        addWithPositionVariants(list, "C96", "向上跳空三法_UpsideGapThreeMethods", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null || d.getT4() == null) return false;
            StockDetail t4 = d.getT4(), t3 = d.getT3();
            return Boolean.TRUE.equals(t4.getIsRed())
                    && moreThan(t3.getLowPrice(), t4.getHighPrice())
                    && Boolean.TRUE.equals(t3.getIsGreen()) && Boolean.TRUE.equals(d.getT2().getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen())
                    && moreThan(d.getEndPrice(), t4.getHighPrice())
                    && moreThan(d.getLowPrice(), t4.getLowPrice());
        });
        addWithPositionVariants(list, "C97", "铺垫形态_MatHold", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null || d.getT4() == null) return false;
            StockDetail t4 = d.getT4();
            return Boolean.TRUE.equals(t4.getIsRed())
                    && Boolean.TRUE.equals(d.getT3().getIsGreen()) && Boolean.TRUE.equals(d.getT2().getIsGreen()) && Boolean.TRUE.equals(d.getT1().getIsGreen())
                    && moreThan(d.getEndPrice(), t4.getHighPrice())
                    && moreThan(d.getLowPrice(), t4.getLowPrice())
                    && moreThan(d.getT1().getLowPrice(), t4.getLowPrice());
        });
        addWithPositionVariants(list, "C98", "三明治看涨_StickSandwich", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getLastPrice() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            BigDecimal closeDiff = subtract(d.getEndPrice(), t2.getEndPrice());
            return Boolean.TRUE.equals(t2.getIsRed()) && Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && moreThan(t1.getLowPrice(), t2.getLowPrice()) && moreThan(t1.getLowPrice(), d.getLowPrice())
                    && closeDiff != null && lessThan(divide(closeDiff.abs(), d.getLastPrice()), "0.01");
        });
        addWithPositionVariants(list, "C99", "突破形态_BullishBreakaway", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null || d.getT4() == null) return false;
            StockDetail t4 = d.getT4();
            return Boolean.TRUE.equals(t4.getIsGreen())
                    && Boolean.TRUE.equals(d.getT3().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed())
                    && Boolean.TRUE.equals(d.getIsRed())
                    && moreThan(d.getEndPrice(), t4.getHighPrice())
                    && moreThan(d.getLowPrice(), t4.getLowPrice());
        });
        addWithPositionVariants(list, "C100", "蜻蜓十字_看涨DragonflyDoji", (StockDetail d) -> {
            if (d.getLowShadowLen() == null || d.getUpShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsTenStar())
                    && moreThan(d.getLowShadowLen(), "0.03")
                    && lessThan(d.getUpShadowLen(), "0.005");
        });
        addWithPositionVariants(list, "C101", "看涨吞没加强_BullishEngulfingStrong", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal t1Body = subtract(t1.getStartPrice(), t1.getEndPrice());
            return Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(t1.getIsGreen())
                    && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), t1.getStartPrice())
                    && d.getEntityLen() != null && t1.getEntityLen() != null
                    && moreThan(d.getEntityLen(), multiply(t1.getEntityLen(), "1.5"));
        });
        addWithPositionVariants(list, "C102", "红三兵加强_ThreeSoldiersStrong", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed())
                    && moreThan(d.getEndPrice(), d.getT1().getEndPrice())
                    && moreThan(d.getT1().getEndPrice(), d.getT2().getEndPrice())
                    && d.getEntityLen() != null && d.getT1().getEntityLen() != null && d.getT2().getEntityLen() != null
                    && moreThan(d.getEntityLen(), "0.02") && moreThan(d.getT1().getEntityLen(), "0.02") && moreThan(d.getT2().getEntityLen(), "0.02");
        });
        addWithPositionVariants(list, "C103", "刺透加强_PiercingStrong", (StockDetail d) -> {
            StockDetail t1 = d.getT1();
            if (t1 == null || t1.getStartPrice() == null || t1.getEndPrice() == null) return false;
            BigDecimal mid = add(t1.getStartPrice(), t1.getEndPrice());
            if (mid == null) return false;
            mid = mid.divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), mid)
                    && lessThan(d.getEndPrice(), t1.getStartPrice())
                    && d.getEntityLen() != null && moreThan(d.getEntityLen(), "0.02");
        });
        addWithPositionVariants(list, "C104", "启明星加强_MorningStarStrong", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            BigDecimal t2Mid = add(t2.getStartPrice(), t2.getEndPrice());
            if (t2Mid == null) return false;
            t2Mid = t2Mid.divide(BigDecimal.valueOf(2), 4, java.math.RoundingMode.HALF_UP);
            return Boolean.TRUE.equals(t2.getIsGreen())
                    && (Boolean.TRUE.equals(t1.getIsTenStar()) || (t1.getEntityLen() != null && lessThan(t1.getEntityLen(), "0.02")))
                    && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(t1.getEndPrice(), t2.getEndPrice())
                    && moreThan(d.getEndPrice(), t2Mid)
                    && d.getEntityLen() != null && moreThan(d.getEntityLen(), "0.03");
        });

        // ========== 偏看涨形态 第二批 ==========
        addWithPositionVariants(list, "C105", "跳空低开十字_GappingDownDoji", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            return Boolean.TRUE.equals(d.getIsTenStar()) && lessThan(d.getHighPrice(), d.getT1().getLowPrice());
        });
        addWithPositionVariants(list, "C106", "收盘光头阳线_ClosingWhiteMarubozu", (StockDetail d) -> {
            if (d.getUpShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && lessThan(d.getUpShadowLen(), "0.005") && moreThan(d.getEntityLen(), "0.02");
        });
        addWithPositionVariants(list, "C107", "开盘光头阳线_OpeningWhiteMarubozu", (StockDetail d) -> {
            if (d.getLowShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && lessThan(d.getLowShadowLen(), "0.005") && moreThan(d.getEntityLen(), "0.02");
        });
        addWithPositionVariants(list, "C108", "长白蜡烛_LongWhiteCandle", (StockDetail d) -> {
            if (d.getEntityPert() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && moreThan(d.getEntityPert(), "0.65");
        });
        addWithPositionVariants(list, "C109", "纺锤阳线看涨_BullishSpinningTop", (StockDetail d) -> {
            if (d.getEntityPert() == null || d.getUpShadowLen() == null || d.getLowShadowLen() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && isInRangeNotEquals(d.getEntityPert(), "0.1", "0.4")
                    && moreThan(d.getUpShadowLen(), "0.02") && moreThan(d.getLowShadowLen(), "0.02");
        });
        addWithPositionVariants(list, "C110", "下降鹰_DescendingHawk", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            StockDetail t1 = d.getT1(), t2 = d.getT2();
            return Boolean.TRUE.equals(t2.getIsGreen()) && Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(t1.getEndPrice(), t2.getEndPrice()) && lessThan(d.getStartPrice(), t1.getEndPrice())
                    && moreThan(d.getEndPrice(), t2.getStartPrice());
        });
        addWithPositionVariants(list, "C111", "迟疑形态_Deliberation", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed())
                    && moreThan(d.getEndPrice(), d.getT1().getEndPrice()) && moreThan(d.getT1().getEndPrice(), d.getT2().getEndPrice())
                    && d.getEntityLen() != null && d.getT1().getEntityLen() != null && d.getT2().getEntityLen() != null
                    && lessThan(d.getEntityLen(), d.getT1().getEntityLen()) && lessThan(d.getEntityLen(), d.getT2().getEntityLen());
        });
        addWithPositionVariants(list, "C112", "三星看涨_BullishTriStar", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            return Boolean.TRUE.equals(d.getIsTenStar()) && Boolean.TRUE.equals(d.getT1().getIsTenStar()) && Boolean.TRUE.equals(d.getT2().getIsTenStar())
                    && moreThan(d.getLowPrice(), d.getT1().getHighPrice()) && moreThan(d.getT1().getLowPrice(), d.getT2().getHighPrice());
        });
        addWithPositionVariants(list, "C113", "看涨孕线加强_BullishHaramiStrong", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getEndPrice(), t1.getStartPrice()) && moreThan(d.getStartPrice(), t1.getEndPrice())
                    && t1.getEntityLen() != null && moreThan(t1.getEntityLen(), "0.03");
        });
        addWithPositionVariants(list, "C114", "双底看涨_TweezerBottomBullish", (StockDetail d) -> {
            if (d.getT1() == null || d.getLastPrice() == null) return false;
            BigDecimal diff = subtract(d.getLowPrice(), d.getT1().getLowPrice());
            return diff != null && lessThan(divide(diff.abs(), d.getLastPrice()), "0.01")
                    && Boolean.TRUE.equals(d.getIsRed()) && moreThan(d.getEndPrice(), d.getStartPrice());
        });
        addWithPositionVariants(list, "C115", "颈线看涨_OnNeckBullish", (StockDetail d) -> {
            if (d.getT1() == null || d.getLastPrice() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal diff = subtract(d.getEndPrice(), t1.getLowPrice());
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && diff != null && lessThan(divide(diff.abs(), d.getLastPrice()), "0.005");
        });
        addWithPositionVariants(list, "C116", "刺入看涨_InNeckBullish", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            BigDecimal bodyLen = subtract(t1.getStartPrice(), t1.getEndPrice());
            BigDecimal closeAbove = subtract(d.getEndPrice(), t1.getEndPrice());
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsRed())
                    && lessThan(d.getStartPrice(), t1.getEndPrice()) && moreThan(d.getEndPrice(), t1.getEndPrice())
                    && bodyLen != null && bodyLen.compareTo(BigDecimal.ZERO) > 0
                    && closeAbove != null && closeAbove.compareTo(BigDecimal.ZERO) > 0
                    && lessThan(closeAbove, multiply(bodyLen, "0.15"));
        });
        addWithPositionVariants(list, "C117", "六连红", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null || d.getT4() == null || d.getT5() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed())
                    && Boolean.TRUE.equals(d.getT3().getIsRed()) && Boolean.TRUE.equals(d.getT4().getIsRed()) && Boolean.TRUE.equals(d.getT5().getIsRed());
        });
        addWithPositionVariants(list, "C118", "七连红", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null || d.getT3() == null || d.getT4() == null || d.getT5() == null || d.getT6() == null) return false;
            return Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(d.getT1().getIsRed()) && Boolean.TRUE.equals(d.getT2().getIsRed())
                    && Boolean.TRUE.equals(d.getT3().getIsRed()) && Boolean.TRUE.equals(d.getT4().getIsRed())
                    && Boolean.TRUE.equals(d.getT5().getIsRed()) && Boolean.TRUE.equals(d.getT6().getIsRed());
        });
        addWithPositionVariants(list, "C119", "上升窗口支撑_RisingWindowSupport", (StockDetail d) -> {
            if (d.getT1() == null || d.getT2() == null) return false;
            return moreThan(d.getT1().getLowPrice(), d.getT2().getHighPrice())
                    && Boolean.TRUE.equals(d.getIsRed())
                    && moreThan(d.getLowPrice(), d.getT1().getLowPrice());
        });
        addWithPositionVariants(list, "C120", "阳包阴缩量", (StockDetail d) -> {
            if (d.getT1() == null || d.getFiveDayDealQuantity() == null || d.getDealQuantity() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(t1.getIsGreen())
                    && lessThan(d.getStartPrice(), t1.getEndPrice()) && moreThan(d.getEndPrice(), t1.getStartPrice())
                    && lessThan(d.getDealQuantity(), multiply(d.getFiveDayDealQuantity(), "1.2"));
        });
        addWithPositionVariants(list, "C121", "低位锤子_LowPositionHammer", (StockDetail d) -> {
            if (d.getPosition20() == null || d.getLowShadowLen() == null || d.getEntityLen() == null || d.getUpShadowLen() == null) return false;
            return lessThan(d.getPosition20(), "0.35")
                    && moreThan(d.getLowShadowLen(), multiply(d.getEntityLen(), "2"))
                    && lessThan(d.getUpShadowLen(), "0.02")
                    && Boolean.TRUE.equals(d.getIsRed());
        });
        addWithPositionVariants(list, "C122", "低位吞没_LowPositionEngulfing", (StockDetail d) -> {
            if (d.getT1() == null || d.getPosition20() == null) return false;
            StockDetail t1 = d.getT1();
            return lessThan(d.getPosition20(), "0.4")
                    && Boolean.TRUE.equals(d.getIsRed()) && Boolean.TRUE.equals(t1.getIsGreen())
                    && lessThan(d.getStartPrice(), t1.getEndPrice()) && moreThan(d.getEndPrice(), t1.getStartPrice());
        });
        addWithPositionVariants(list, "C123", "超卖区蜻蜓十字_WROversoldDragonfly", (StockDetail d) -> {
            if (d.getWr() == null || d.getLowShadowLen() == null || d.getUpShadowLen() == null) return false;
            return lessThan(d.getWr(), "-70")
                    && Boolean.TRUE.equals(d.getIsTenStar())
                    && moreThan(d.getLowShadowLen(), "0.03")
                    && lessThan(d.getUpShadowLen(), "0.005");
        });
        addWithPositionVariants(list, "C124", "超卖区锤子_WROversoldHammer", (StockDetail d) -> {
            if (d.getWr() == null || d.getLowShadowLen() == null || d.getEntityLen() == null || d.getUpShadowLen() == null) return false;
            return lessThan(d.getWr(), "-70")
                    && moreThan(d.getLowShadowLen(), multiply(d.getEntityLen(), "2"))
                    && lessThan(d.getUpShadowLen(), "0.02")
                    && Boolean.TRUE.equals(d.getIsRed());
        });
        addWithPositionVariants(list, "C125", "看涨孕线十字加强_BullishHaramiCrossStrong", (StockDetail d) -> {
            if (d.getT1() == null) return false;
            StockDetail t1 = d.getT1();
            return Boolean.TRUE.equals(t1.getIsGreen()) && Boolean.TRUE.equals(d.getIsTenStar())
                    && lessThan(d.getHighPrice(), t1.getStartPrice()) && moreThan(d.getLowPrice(), t1.getEndPrice())
                    && t1.getEntityLen() != null && moreThan(t1.getEntityLen(), "0.03");
        });

        return list;
    }
    public Map<StrategyEnum, List<StockDetail>> calcByStrategy(List<StrategyEnum> strategyList) throws ExecutionException, InterruptedException {
        Map<StrategyEnum, List<StockDetail>> strategyToCalcMap = new ConcurrentHashMap<>();
        log.info("开始计算");
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (List<String> part : CommonService.stockCodePartList) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (String stockCode : part) {
                    List<StockDetail> stockDetails = CommonService.codeToDetailMap.get(stockCode);
                    if (stockDetails.size() < 60) {
                        return;
                    }
                    for (StrategyEnum strategy : strategyList) {
                        for (int i = 0; i < stockDetails.size() - 60; i++) {
                            StockDetail stockDetail = stockDetails.get(i);
                            if (moreThan(stockDetail.getPricePert(), "0.097")
                                    || Objects.isNull(stockDetail.getNext1())
                                    || Objects.isNull(stockDetail.getT10())
                                    || Objects.isNull(stockDetail.getT10().getSixtyDayLine())
                                    || !strategy.getRunFunc().apply(stockDetail)) {
                                continue;
                            }
                            strategyToCalcMap.computeIfAbsent(strategy, v -> new CopyOnWriteArrayList<>()).add(stockDetail);
                        }
                    }
                }
            }, ioThreadPool);
            futures.add(future);
        }
        CompletableFuture<Void> allTask = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allTask.get();

        strategyToCalcMap.forEach((strategyEnum, list) -> {
            StrategyWin strategyWin = StrategyWin.createByStrategyName(strategyEnum);
            strategyWin.setLevel(0);
            list.forEach(strategyWin::addToResult);
            strategyWin.fillData1();
            strategyWin.fillData2();
            strategyWinService.save(strategyWin);
        });
        log.info("结束计算");
        return strategyToCalcMap;
    }
}
