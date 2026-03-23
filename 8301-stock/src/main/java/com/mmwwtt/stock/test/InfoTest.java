package com.mmwwtt.stock.test;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * 自动化测试
 * 获取财联社的电报内容
 */
@Slf4j
public class InfoTest {

    private static final Set<String> DATES = new HashSet<>();

    static {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        String todayStr = today.format(DateTimeFormatter.ofPattern("财联社M月d日电"));      // "03-19"
        String yesterdayStr = yesterday.format(DateTimeFormatter.ofPattern("财联社M月d日电")); // "03-18"

        DATES.add(todayStr);
        DATES.add(yesterdayStr);
    }

    @Test
    @DisplayName("获取财联社的电报内容")
    public void builInfo() {
        System.setProperty("webdriver.chrome.driver", "D:\\1.moweitao\\1.java\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless"); // 如果需要无头模式（后台运行），取消注释此行

        WebDriver driver = new ChromeDriver(options);
        String filePath = "src/main/resources/file/预测的股票.txt";
        File file = new File(filePath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            // 设置隐式等待 (全局等待元素出现)
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            // 最大化窗口
            driver.manage().window().maximize();
            driver.get("https://www.cls.cn/telegraph");

            WebElement btn = driver.findElement(By.xpath("//div[normalize-space()='加载更多']"));
            btn.click();
            Thread.sleep(1000);

            //懒加载剩余内容
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (int i = 1; i <= 40; i++) {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(500);
            }
            log.info("数据加载完成, 开始进行抓取");
            // 抓取数据
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) js.executeScript(
                    "return Array.from(document.querySelectorAll('.c-de0422')).map(function(el){return el.innerText.trim();});");
            if (CollectionUtils.isEmpty(list)) {
                return;
            }

            for (String str : list) {
                if (str.length() < 16) {
                    continue;
                }
                if (DATES.stream().anyMatch(str::contains)) {
                    str = str.replaceAll("\\n\\s*\\n", "\n");
                    fos.write(str.getBytes());
                }
            }
            log.info("数据抓取结束 ");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("正在关闭浏览器...");
            driver.quit();
        }
    }
}
