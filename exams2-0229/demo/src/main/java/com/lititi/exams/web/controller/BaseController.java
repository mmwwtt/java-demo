package com.lititi.exams.web.controller;

import com.lititi.exams.commons2.log.LttLogger;
import com.lititi.exams.commons2.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import javax.management.timer.Timer;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseController {

    private final LttLogger logger = new LttLogger(BaseController.class);

    protected Map<String, String> getAllParams(HttpServletRequest request) {
        Map<String, String> paramsMap = new HashMap<String, String>();
        Map<String, String[]> oldParameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : oldParameterMap.entrySet()) {
            // 测试环境使用，不参与签名
            if (StringUtils.equals(entry.getKey(), "serverId")) {
                continue;
            }
            paramsMap.put(entry.getKey(), entry.getValue()[0]);
        }
        return paramsMap;
    }

    /**
     * 解析时间参数,目前仅可能出现如下格式:
     * 毫秒时间戳、yyyy-MM-dd HH:mm:ss、yyyy-MM-dd、yyyy.MM.dd
     */
    protected Long formatTimeStrToLong(String timeStr, boolean endFlag) {
        try {
            if (NumberUtils.isDigits(timeStr)) {
                return Long.parseLong(timeStr);
            } else if (StringUtils.isNotBlank(timeStr)) {
                List<String> dateFormatList = Arrays.asList(DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS,
                        DateUtil.DATE_FORMAT_EN_B_YYYYMMDD, DateUtil.DATE_FORMAT_EN_C_YYYYMMDD);
                for (String format : dateFormatList) {
                    if (DateUtil.checkDate(timeStr, format)) {
                        Long time = DateUtil.stringFormat2LongWithoutError(timeStr, format);
                        if (time != null && endFlag) {
                            if (StringUtils.equals(DateUtil.DATE_FORMAT_EN_B_YYYYMMDDHHMMSS, format)) {
                                time = time + Timer.ONE_SECOND - 1;
                            } else if (StringUtils.equals(DateUtil.DATE_FORMAT_EN_B_YYYYMMDD, format)
                                    || StringUtils.equals(DateUtil.DATE_FORMAT_EN_C_YYYYMMDD, format)) {
                                time = time + Timer.ONE_DAY - 1;
                            }
                        }
                        return time;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("formatTimeStrToLong error,timeStr:" + timeStr + ",endFlag:" + endFlag, e.getMessage());
        }
        return null;
    }

}
