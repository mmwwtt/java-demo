package com.mmwwtt.demo.se.jdk新特性;

import com.mmwwtt.demo.common.util.CommonUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class LocalDateDemo {

    @Test
    public void test() {
        LocalDate now1 = LocalDate.now();                            // 当前日期
        LocalDate now2 = LocalDate.now(ZoneId.of("Asia/Shanghai"));    // 当前日期 (指定时区)
        LocalDate now3 = LocalDate.now(Clock.systemDefaultZone());    // 当前日期 (指定时钟)
        LocalDate localDate = LocalDate.of(2023, 1, 1);                // 指定日期 2023-01-01


        LocalDate now = LocalDate.now();
        int year = now.getYear();						// 获取年份
        int month = now.getMonthValue();				// 获取月份（1-12）
        Month monthEnum = now.getMonth();				// 获取月份的枚举值
        int dayOfMonth = now.getDayOfMonth();			// 获取月份中的第几天（1-31）
        int dayOfYear = now.getDayOfYear();				// 获取一年中的第几天（1-366）
        DayOfWeek dayOfWeek = now.getDayOfWeek();		// 获取现在是星期几
        int lengthOfYear = now.lengthOfYear();			// 获得当年总天数
        int lengthOfMonth = now.lengthOfMonth();		// 获得当月总天数
        long epochDay = now.toEpochDay();				// 与时间纪元（1970年1月1日）相差的天数


        LocalDate localDate1 = now.plusDays(1);			// 给当前时间加一天
        LocalDate localDate2 = now.plusDays(1);			// 给当前时间加一周
        LocalDate localDate3 = now.plusMonths(1);		// 给当前时间加一月
        LocalDate localDate4 = now.plusYears(1);		// 给当前时间加一年
        LocalDate localDate5 = now.minusDays(1);		// 给当前时间减一天
        LocalDate localDate6 = now.minusWeeks(1);		// 给当前时间减一周
        LocalDate localDate7 = now.minusMonths(1);		// 给当前时间减一月
        LocalDate localDate8 = now.minusYears(1);		// 给当前时间减一年


        LocalDate localDate9 = now.withYear(2020);		// 修改日期对象年份为2020
        LocalDate localDate10 = now.withMonth(1);		// 修改日期对象月份为1
        LocalDate localDate11 = now.withDayOfMonth(1);	// 修改日期对象的日期(一月中的第几天)
        LocalDate localDate12 = now.withDayOfYear(1);	// 修改日期对象的日期(一年中的第几天)

        CommonUtils.println(now1, now2, now3, localDate);
    }

    @Test
    public void dateFormatDemo() {

        //Date转DateTime
        Date date = new Date();
        LocalDate dateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //DateTime转String
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("YYYY-MM-dd hh:mm:ss");
        String formatStr = localDateTime.format(dateTimeFormatter);

        CommonUtils.println(formatStr, dateTime);
    }

}
