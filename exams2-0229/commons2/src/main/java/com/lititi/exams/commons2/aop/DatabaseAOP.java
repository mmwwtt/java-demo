package com.lititi.exams.commons2.aop;


import com.lititi.exams.commons2.annotation.Master;
import com.lititi.exams.commons2.annotation.Slave;
import com.lititi.exams.commons2.constant.Constant;
import com.lititi.exams.commons2.datasource.DataSourceKey;
import com.lititi.exams.commons2.exception.LttException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class DatabaseAOP {
    @Pointcut(value = "execution(* com.lititi.exams.*.dao..*.*(..))")
    public void pointCut() {

    }

    @Before("pointCut()")
    public void before(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        boolean isMasterExist = method.isAnnotationPresent(Master.class);
        boolean isSlaveExist = method.isAnnotationPresent(Slave.class);
        if (isMasterExist && isSlaveExist) {
            throw new LttException("@Master和@Slave！不能同时存在");
        }
        if (isSlaveExist) {
            DataSourceKey.setKey(Constant.SLAVE);
            return;
        }
        DataSourceKey.setKey(Constant.MASTER);
    }

}
