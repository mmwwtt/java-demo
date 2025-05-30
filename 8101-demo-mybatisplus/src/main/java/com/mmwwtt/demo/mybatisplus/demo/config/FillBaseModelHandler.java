package com.mmwwtt.demo.mybatisplus.demo.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自动填充更新时间和创建时间
 * 和实体中的@TableField注解连用
 */
@Slf4j
@Component
public class FillBaseModelHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill.....");
        this.setFieldValByName("createdDate", LocalDateTime.now(), metaObject);
        this.setFieldValByName("lastUpdatedDate", LocalDateTime.now(), metaObject);
    }

    /**
     * 更新时的填充策略
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill.....");
        this.setFieldValByName("lastUpdatedDate", LocalDateTime.now(), metaObject);
    }

}
