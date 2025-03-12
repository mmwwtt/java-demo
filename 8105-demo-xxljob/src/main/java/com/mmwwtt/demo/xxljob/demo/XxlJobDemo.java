package com.mmwwtt.demo.xxljob.demo;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 启动xxl-job-master项目
 * http://localhost:8002/xxl-job-admin/
 * 默认账号密码：admin/123456
 */
@Component
public class XxlJobDemo {
    private static final Logger logger = LoggerFactory.getLogger(XxlJobDemo.class);

    @XxlJob("myDemoJobHandler")
    public ReturnT<String> paramJobHandler(String param) {
        String jobParam = XxlJobHelper.getJobParam();
        logger.info("Received param: {}", param);
        // 对参数进行处理
        return ReturnT.SUCCESS;
    }
}
