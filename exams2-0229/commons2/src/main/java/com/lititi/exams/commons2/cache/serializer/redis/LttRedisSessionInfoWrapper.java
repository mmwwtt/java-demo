package com.lititi.exams.commons2.cache.serializer.redis;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NOTE: 该类仅用于tomcat redis session 共享数据，读取之后的封装， 暂时不用于修改session修改
 *
 * @author Kevin
 * @version LttRedisSessionInfoWrapper.java, v 0.1 2021年2月13日 上午11:38:52
 * @company 杭州利提提
 */
public class LttRedisSessionInfoWrapper implements Serializable {

    private static final long serialVersionUID = -4668434382868131773L;
    // createTime,creationTime 2个字段的值是一样的， 这样定义是因为 redis Session Manager有自己的createTime;
    /** */
    // private Long createTime;
    /**
     *
     */
    private Long creationTime;
    /**
     * 上次操作时间
     */
    private Long lastAccessedTime;
    private Integer maxInactiveInterval;
    /**
     * 是否新加的
     */
    private Boolean isNew;
    private Boolean isValid;
    private Long thisAccessedTime;
    /**
     * session id/redis session key
     */
    private String id;

    /**
     * 属性的个数
     */
    private ConcurrentHashMap<String, Object> attributes = new ConcurrentHashMap<String, Object>();

    public Long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Long creationTime) {
        this.creationTime = creationTime;
    }

    public Long getLastAccessedTime() {
        return lastAccessedTime;
    }

    public void setLastAccessedTime(Long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    public Integer getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    public void setMaxInactiveInterval(Integer maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Long getThisAccessedTime() {
        return thisAccessedTime;
    }

    public void setThisAccessedTime(Long thisAccessedTime) {
        this.thisAccessedTime = thisAccessedTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ConcurrentHashMap<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(ConcurrentHashMap<String, Object> attributes) {
        this.attributes = attributes;
    }

}
