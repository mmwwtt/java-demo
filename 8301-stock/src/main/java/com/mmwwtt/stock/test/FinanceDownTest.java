package com.mmwwtt.stock.test;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mmwwtt.stock.entity.Finance;
import com.mmwwtt.stock.service.impl.FinanceServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 融资融券数据下载
 */
@SpringBootTest
@Slf4j
public class FinanceDownTest {

    @Autowired
    private FinanceServiceImpl financeService;

    @Test
    public void downFinance() {

        financeService.remove(new QueryWrapper<>());
        System.setProperty("webdriver.chrome.driver", "D:\\1.moweitao\\1.java\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless"); // 如果需要无头模式（后台运行），取消注释此行

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();
        //打开上海证券交易所官网
        driver.get("https://www.sse.com.cn/market/othersdata/margin/sum/");

        List<Finance> res = new ArrayList<>(500000);
        financeService.saveBatch(res);
    }
}
