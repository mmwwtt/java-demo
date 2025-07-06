package com.lititi.exams.web.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.lititi.exams.commons2.enumeration.RedirectPageType;
import com.lititi.exams.commons2.exception.LttException;
import com.lititi.exams.commons2.log.LttLogger;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.lititi.exams.commons2.constant.Constant.TOKEN_HEADER;
import static com.lititi.exams.commons2.enumeration.ExceptionCode.*;
import static com.lititi.exams.commons2.result.JsonCodeEnum.NOT_LOGIN;

@Component
public class LoginInterceptor implements HandlerInterceptor {


    private static final LttLogger logger = new LttLogger(LoginInterceptor.class);

    private static final List<String> allExcludeUri = new ArrayList<>();// 不进行任何判断，直接放行
    private static final List<String> needTokenUri = new ArrayList<>();// 请求头需要带token，才能放行

    static {
        // 不进行任何判断，直接放行
        allExcludeUri.add("/test");
        needTokenUri.add("/content/publish");
        needTokenUri.add("/content/list");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) {
        String uri = request.getRequestURI();
        String token = request.getHeader(TOKEN_HEADER);

        if(StringUtils.isEmpty(uri)){
            return false;
        }

        if(!uri.toLowerCase().startsWith("/test") && !uri.toLowerCase().startsWith("/content")){
            return false;
        }

        // 不进行任何判断，直接放行
        if (checkUri(uri, allExcludeUri)) {
            return true;
        }

        if(checkUri(uri, needTokenUri)){
            if(StringUtils.isEmpty(token)){
                return false;
            }
        }
        
        return false;
    }

    private boolean checkUri(String uri, List<String> allExcludeUri) {
        if (StringUtils.isEmpty(uri) || CollectionUtils.isEmpty(allExcludeUri)) {
            return false;
        }

        String url1 = uri.toLowerCase();
        for (String excludeUrl : allExcludeUri) {
            if (!StringUtils.isEmpty(excludeUrl) && url1.startsWith(excludeUrl.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkUriEqual(String uri, List<String> allExcludeUri) {
        if (StringUtils.isEmpty(uri) || CollectionUtils.isEmpty(allExcludeUri)) {
            return false;
        }
        for (String excludeUrl : allExcludeUri) {
            if (uri.equals(excludeUrl)) {
                return true;
            }
        }
        return false;
    }

    private void returnNotLoginJSONObject(HttpServletRequest request, HttpServletResponse response) {
        // 清空session
        // logger.info("用户会进入登录页面");
        request.getSession().invalidate();
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject res = new JSONObject();
        res.put("redirectPageType", RedirectPageType.LOGIN.getCode());
        res.put("result", 0);
        res.put("message", NOT_LOGIN.getMessage());
        try (PrintWriter out = response.getWriter()) {
            out.append(res.toString());
        } catch (IOException e) {
            throw new LttException(SERVER_FAILURE);
        }
    }

    private void returnNotOperatePhonePageJSONObject(HttpServletRequest request, HttpServletResponse response) {
        // logger.info("用户需要绑定手机号");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject res = new JSONObject();
        res.put("redirectPageType", RedirectPageType.BIND_MOBILE_PHONE.getCode());
        res.put("result", 0);
        res.put("message", NOT_BIND_MOBILE_PHONE.getMessage());
        try (PrintWriter out = response.getWriter()) {
            out.append(res.toString());
        } catch (IOException e) {
            throw new LttException(SERVER_FAILURE);
        }
    }

    private void returnNotRegistMemberPageJSONObject(HttpServletRequest request, HttpServletResponse response) {
        // logger.info("用户需要注册会员");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        JSONObject res = new JSONObject();
        // 重定向页面类型
        res.put("redirectPageType", RedirectPageType.REGIST_MEMBER.getCode());
        res.put("result", 0);
        res.put("message", NOT_REGIST_MEMBER.getMessage());
        try (PrintWriter out = response.getWriter()) {
            out.append(res.toString());
        } catch (IOException e) {
            throw new LttException(SERVER_FAILURE);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
        throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
//        UserContext.remove();
    }

}
