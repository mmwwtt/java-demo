package com.mmwwtt.demo.ee.mybatisPlus;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

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
        this.setFieldValByName("createdDate", new Date(), metaObject);
        this.setFieldValByName("lastUpdatedDate", new Date(), metaObject);
    }

    /**
     * 更新时的填充策略
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill.....");
        this.setFieldValByName("lastUpdatedDate", new Date(), metaObject);
    }

}
