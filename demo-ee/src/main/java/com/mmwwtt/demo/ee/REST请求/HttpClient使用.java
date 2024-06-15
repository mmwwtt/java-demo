package com.mmwwtt.demo.ee.REST请求;


import org.junit.jupiter.api.Test;

public class HttpClient使用 {
    @Test
    public void testLinked() throws Exception {
        // 创建HttpClient对象
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//
//        // 创建GET请求
//        HttpGet httpGet = new HttpGet("https://bbs.zgogc.com/2048/thread.php?fid-15-page-1.html");
//        httpGet.setHeader("use-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");
//
//        // 获取响应结果
//        CloseableHttpResponse response = httpClient.execute(httpGet);
//
//        if (response.getStatusLine().getStatusCode() == 200) {
//            String html = EntityUtils.toString(response.getEntity(), "UTF-8");
//            System.out.println(html);
//        }
//
//        httpClient.close();
//        response.close();
    }
}
