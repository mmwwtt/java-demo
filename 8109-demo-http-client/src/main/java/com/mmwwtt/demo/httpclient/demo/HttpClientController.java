package com.mmwwtt.demo.httpclient.demo;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/http-client")
public class HttpClientController {
    @GetMapping("/get-demo")
    public String getDemo() {
        return "hello get request";
    }

    @GetMapping("/get-request")
    public void getRequest() {
        // 创建 HttpClient 实例
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建 HttpGet 请求实例
        HttpGet httpGet = new HttpGet("http://localhost:8109/http-client/get-demo");

        try {
            // 执行请求并获取响应
            CloseableHttpResponse response = httpClient.execute(httpGet);
            // 获取响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Status Code: " + statusCode);

            // 获取响应实体并将其转换为字符串
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("Response Body: " + responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            httpGet.releaseConnection();
        }
    }

    @PostMapping("/post-demo")
    public String postDemo(@RequestBody Object object) {
        return "hello post request";
    }

    @GetMapping("/post-request")
    public void postRequest(){
        // 创建 HttpClient 实例
        HttpClient httpClient = HttpClients.createDefault();
        // 创建 HttpPost 请求实例
        HttpPost httpPost = new HttpPost("http://localhost:8109/http-client/post-demo");

        // 创建参数列表
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("key1", "value1"));
        params.add(new BasicNameValuePair("key2", "value2"));

        try {
            // 设置请求实体
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            // 执行请求并获取响应
            HttpResponse response = httpClient.execute(httpPost);
            // 获取响应状态码
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("Status Code: " + statusCode);

            // 获取响应实体并将其转换为字符串
            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("Response Body: " + responseBody);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 释放资源
            httpPost.releaseConnection();
        }
    }
}
