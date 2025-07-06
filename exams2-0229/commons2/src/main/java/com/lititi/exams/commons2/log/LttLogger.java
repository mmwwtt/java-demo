package com.lititi.exams.commons2.log;


import com.lititi.exams.commons2.utils.CommonUtil;
import com.lititi.exams.commons2.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;


/**
 * 统一的日志输出对象
 */
public class LttLogger {

    private final Logger logger;
    private String prefix;

    /**
     *
     * @param clazz 将类名当作日志名字
     */
    public LttLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz.getName());
    }

    /**
     * @param logName 设置日志名字
     */
    public LttLogger(String logName) {
        this.logger = LoggerFactory.getLogger(logName);
    }

    private String buildPrefix() {
        prefix = "";
        return prefix;
    }

    /**
     * 打印出一个复杂对象，简单类型不建议用该方法。且该方法是debug级别
     *
     * @param name 打印的名称标识
     * @param object 要打印的对象
     */
    public void dump(String name, Object object) {
        long start = System.currentTimeMillis();
        try {
            StringBuffer sb = new StringBuffer("dump [\"" + name + "\"] : ");
            if (object == null) {
                sb.append("[null]");
            } else {
                sb.append(object.getClass().getName() + "@" + object.hashCode()).append("\n");
                sb.append(LogUtil.parse(object, 1, null, null));
            }
            sb.append("\n").append("dump finished");
            long end = System.currentTimeMillis();
            logger.debug(
                CommonUtil.concat(buildPrefix(), CommonUtil.concat(sb.toString()), LogUtil.buildTime(start, end)));

        } catch (Exception e) {
            logger.debug(name, e);
        }
    }

    /**
     * 打印debug级别日志
     *
     * @param msgArr
     *            :多个日志信息
     */
    public void debug(String className, String methodName, Object... msgArr) {
        if (LogUtil.isDebugEnabled()) {
            logger.debug(concatInfo(className, methodName, msgArr));
        }
    }

    /**
     * 打印warn级别日志
     *
     * @param msgArr
     *            :多个日志信息
     */
    public void warn(String className, String methodName, Object... msgArr) {
        logger.warn(concatInfo(className, methodName, msgArr));
    }

    public void warn(String msg) {
        logger.warn(msg);
    }

    public void warn(String msg, Throwable t) {
        logger.warn(msg, t);
    }

    public void error(Object... msgArr) {
        logger.error(CommonUtil.concat(buildPrefix(), CommonUtil.concat(msgArr)));
    }

    public void error(Throwable t, Object... msgArr) {
        logger.error(CommonUtil.concat(buildPrefix(), CommonUtil.concat(msgArr)), t);
    }

    /**
     *
     * @param className 抛出异常的类名
     * @param methodName 抛出异常的方法名
     * @param message 日志信息
     * @param t 抛出的异常
     */

    public void error(String className, String methodName, Object message, Throwable t) {
        logger.error(concatInfo(className, methodName, message), t);
    }

    /**
     *
     * @param className 抛出异常的类名
     * @param methodName 抛出异常的方法名
     * @param t 抛出的异常
     */

    public void error(String className, String methodName, Throwable t) {
        this.error(className, methodName, t.getMessage(), t);
    }

    public void error(String msg, Throwable t) {
        logger.error(msg, t);
    }

    /**
     * 打印info级别日志
     */
    public void info(String className, String methodName, Object... msgArr) {
        if (LogUtil.isInfoEnabled()) {
            logger.info(concatInfo(className, methodName, msgArr));
        }
    }

    public void info(String msg) {
        if (LogUtil.isInfoEnabled()) {
            logger.info(msg);
        }
    }

    private String concatInfo(String className, String methodName, Object... msgArr) {
        return className + "[" + methodName + "]" + CommonUtil.concat(buildPrefix(), CommonUtil.concat(msgArr));
    }

    /**
     * 打印JSON对象
     */
    public void dumpJsonObject(String name, Object object) {
        if (object != null) {
            long start = System.currentTimeMillis();
            String sb = "dump [\"" + name + "\"] : " + JSON.toJSONString(object) + "\n" + "dumpJsonObject finished";
            long end = System.currentTimeMillis();
            logger.debug(
                CommonUtil.concat(buildPrefix(), CommonUtil.concat(sb), LogUtil.buildTime(start, end)));
        }
    }

}
