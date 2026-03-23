package com.mmwwtt.stock.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 自动化测试
 * 获取财联社的电报内容
 */
@Slf4j
public class InfoTest {

    @Test
    @DisplayName("获取财联社的电报内容")
    public void builInfo()  {
        System.setProperty("webdriver.chrome.driver", "D:\\1.moweitao\\1.java\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("--headless"); // 如果需要无头模式（后台运行），取消注释此行

        WebDriver driver = new ChromeDriver(options);

        try {
            // 设置隐式等待 (全局等待元素出现)
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            // 最大化窗口
            driver.manage().window().maximize();
            driver.get("https://www.cls.cn/telegraph");

            WebElement btn = driver.findElement(By.xpath("//div[normalize-space()='加载更多']"));
            btn.click();
            Thread.sleep(1000);

//            //懒加载剩余内容
            JavascriptExecutor js = (JavascriptExecutor) driver;
            for (int i = 1; i <= 50; i++) {
                js.executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(500);
            }

            //抓取数据
            List<String> list = Collections.synchronizedList(new ArrayList<>());
            List<WebElement> newsBoxes = driver.findElements(By.className("telegraph-content-box"));
            newsBoxes.parallelStream().forEach(box-> {
                WebElement strongElement = box.findElement(By.tagName("strong"));
                String info = (String) js.executeScript(
                                "return arguments[0].parentNode.innerText.substring(arguments[0].innerText.length);",
                                strongElement
                        );
                list.add(info);
            });
            String res = list.stream().sorted(String::compareTo).collect(Collectors.joining("\n"));


            log.info("数据抓取结束 ");
            log.info("{}", res);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("正在关闭浏览器...");
            driver.quit();
        }

    }
}
