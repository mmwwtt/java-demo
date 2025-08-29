package com.wwmmtt.demo.springmvc.demo.将数据保存为上下文;

import com.alibaba.fastjson.JSONObject;
import com.mmwwtt.demo.common.entity.BaseInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class UserContextInterceptor extends OncePerRequestFilter {
    private static final String DATA = "data";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);

        JSONObject body = JSONObject.parseObject(cachedRequest.getBody());
        BaseInfo data = body.getObject(DATA, BaseInfo.class);
        UserContext.setBaseInfoId(data == null ? null : data.getBaseInfoId());
        try {
            //继续执行后续过滤器
            filterChain.doFilter(cachedRequest, response);
        } finally {
            UserContext.clear(); // 避免内存泄漏
        }
    }
}
