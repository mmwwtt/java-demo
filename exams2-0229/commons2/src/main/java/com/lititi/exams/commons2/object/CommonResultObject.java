package com.lititi.exams.commons2.object;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lititi.exams.commons2.enumeration.ExceptionCode;
import com.lititi.exams.commons2.enumeration.RedirectPageType;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CommonResultObject {

    private Integer redirectPageType = RedirectPageType.NORMAL.getCode();
    private int result = 1;
    private String message;
    private Map<Object, Object> MData = new HashMap<>();

    public CommonResultObject buildErrorResult(ExceptionCode code) {
        if (code == null) {
            code = ExceptionCode.BUSINESS_ERROR;
        }
        this.result = 0;
        this.message = code.getMessage();
        return this;
    }

    public CommonResultObject buildErrorResult(ExceptionCode code, String message) {
        if (code == null) {
            code = ExceptionCode.BUSINESS_ERROR;
        }
        this.result = 0;
        this.message = code.getMessage() + ":" + message;
        return this;
    }

    public CommonResultObject buildErrorResult(String message) {
        this.result = 0;
        this.message = message;
        return this;
    }

    public CommonResultObject addObject(Object key, Object value) {
        (this.MData == null ? (this.MData = new HashMap<>()) : this.MData).put(key, value);
        return this;
    }

    public CommonResultObject setData(Object value) {
        this.MData.put("data", value);
        return this;
    }

    public void setMData(Map<Object, Object> MData) {
        this.MData = MData;
    }

    public CommonResultObject setMData(Object object) {
        this.MData = JSON.parseObject(JSON.toJSONString(object), new TypeReference<Map<Object, Object>>() {});
        return this;
    }

}
