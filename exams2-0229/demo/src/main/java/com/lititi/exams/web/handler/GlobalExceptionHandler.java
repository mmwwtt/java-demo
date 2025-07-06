package com.lititi.exams.web.handler;

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.alibaba.fastjson.JSON;
import com.lititi.exams.commons2.enumeration.ExceptionCode;
import com.lititi.exams.commons2.exception.LttException;
import com.lititi.exams.commons2.log.LttLogger;
import com.lititi.exams.commons2.object.CommonResultObject;

import static com.lititi.exams.commons2.enumeration.ExceptionCode.*;

/**
 * 全局异常处理器
 *
 *
 * @author zhangjun
 * @date 2021/4/2
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 统一日志处理
     */
    private static final LttLogger log = new LttLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * 
     * @param exception
     *            LttException
     * @return 封装信息的对象
     */
    @ExceptionHandler({LttException.class})
    public CommonResultObject handler(LttException exception) {
        log.error(exception);
        return exception.getCommonResultObject();
    }

    /**
     * 处理SQL异常
     * 
     * @param exception
     *            SQLException
     * @return 封装信息的对象
     */
    @ExceptionHandler({SQLException.class})
    public CommonResultObject handler(SQLException exception) {
        log.error(exception);
        return getResult(DATABASE_CONNECTION_FAILURE);
    }

    /**
     * 接口参数缺失异常
     * 
     * @param exception
     *            异常
     * @return 封装信息的对象
     */
    @ExceptionHandler({ServletRequestBindingException.class})
    public CommonResultObject handler(ServletRequestBindingException exception, HttpServletRequest request) {
        log.error(exception, "url & message:" + request.getRequestURL(),
            " ;Params:" + JSON.toJSONString(request.getParameterMap()));
        return getResult(PARAMETER_MISSING_EXCEPTION);

    }

    /**
     * 接口参数异常
     *
     * @param exception
     *            异常
     * @return 封装信息的对象
     */
    @ExceptionHandler({IllegalArgumentException.class})
    public CommonResultObject handler(IllegalArgumentException exception, HttpServletRequest request) {
        log.error(exception, "url & message:" + request.getRequestURL(),
            " ;Params:" + JSON.toJSONString(request.getParameterMap()));
        return new CommonResultObject().buildErrorResult(exception.getMessage());

    }

    /**
     * 接口参数解析异常(ResponseBody注解标记的参数缺失或者类型不对)
     * 
     * @param exception
     *            异常
     * @return 封装信息的对象
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public CommonResultObject handler(HttpMessageNotReadableException exception) {
        log.error(exception);
        return getResult(HTTP_MESSAGE_NOT_READABLE_EXCEPTION);
    }

    /**
     * 接口参数类型不匹配
     * 
     * @param exception
     *            异常
     * @return 封装信息的对象
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public CommonResultObject handler(MethodArgumentTypeMismatchException exception) {
        log.error(exception);
        return getResult(PARAMETER_TYPE_MISMATCH_EXCEPTION);
    }

    /**
     * 参数校验失败
     * 
     * @param exception
     *            异常
     * @return 封装信息的对象
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public CommonResultObject handler(MethodArgumentNotValidException exception) {
        if (exception.hasErrors()) {
            return new CommonResultObject().buildErrorResult(exception.getAllErrors().get(0).getDefaultMessage());
        }
        log.error(exception);
        return getResult(PARAMETER_TYPE_MISMATCH_EXCEPTION);
    }

    /**
     * 方法不支持
     * 
     * @param exception
     *            异常
     * @return 封装信息的对象
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public CommonResultObject handler(HttpRequestMethodNotSupportedException exception) {
        log.error(exception);
        return getResult(METHOD_NOT_SUPPORTED);
    }

    /**
     * mybatis 相关异常
     * 
     * @param exception
     *            异常
     * @return 封装信息的对象
     */
    @ExceptionHandler(PersistenceException.class)
    public CommonResultObject handler(PersistenceException exception) {
        log.error(exception);
        return getResult(DATABASE_CONNECTION_FAILURE);
    }

    /**
     * 数据库链接异常
     * 
     * @param exception
     *            异常
     * @return 封装信息的对象
     */
    @ExceptionHandler({DataAccessException.class})
    public CommonResultObject handler(DataAccessException exception) {
        log.error(exception);
        return getResult(DATABASE_CONNECTION_FAILURE);
    }

    /**
     * 媒体类型不支持
     * 
     * @param exception
     *            异常
     * @return 封装信息的对象
     */
    @ExceptionHandler({HttpMediaTypeNotSupportedException.class})
    public CommonResultObject handler(HttpMediaTypeNotSupportedException exception) {
        log.error(exception);
        return getResult(HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION);
    }


    /**
     * 处理运行时异常
     *
     * @param exception
     *            运行时异常
     * @return 封装信息的对象
     */
    @ExceptionHandler({RuntimeException.class})
    public CommonResultObject handler(RuntimeException exception) {
        log.error(exception);
        return getResult(BUSINESS_ERROR);
    }

    /**
     * 处理 Throwable
     * 
     * @param throwable
     *            throwable参数
     * @return 封装信息的对象
     */
    @ExceptionHandler({Throwable.class})
    public CommonResultObject handler(Throwable throwable) {
        log.error(throwable);
        return getResult(SERVER_FAILURE);
    }

    @ExceptionHandler({BindException.class})
    public CommonResultObject handler(BindException e) {
        if (e.hasErrors()) {
            return new CommonResultObject().buildErrorResult(e.getAllErrors().get(0).getDefaultMessage());
        }
        log.error(e);
        return getResult(HTTP_MESSAGE_NOT_READABLE_EXCEPTION);
    }

    private CommonResultObject getResult(ExceptionCode code, String message) {
        return new CommonResultObject().buildErrorResult(code, message);
    }

    private CommonResultObject getResult(ExceptionCode code) {
        return new CommonResultObject().buildErrorResult(code);
    }
}
