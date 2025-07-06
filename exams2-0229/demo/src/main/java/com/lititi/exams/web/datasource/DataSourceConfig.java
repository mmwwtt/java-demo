package com.lititi.exams.web.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.lititi.exams.commons2.datasource.DynamicRoutingDataSource;
import com.lititi.exams.commons2.exception.LttException;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.lititi.exams.commons2.constant.Constant.MASTER;
import static com.lititi.exams.commons2.constant.Constant.SLAVE;


@Configuration
@MapperScan(basePackages = DataSourceConfig.BASE_PACKAGE, sqlSessionTemplateRef = "examsSqlSessionTemplate")
public class DataSourceConfig {

    //todo MapperScannerConfigurer实例化在PropertyPlaceholderConfigurer之前，所以value属性无法用在MapperScannerConfigurer实例化中
    public static final String MAPPER_XML_PATH = "classpath:com/lititi/exams/sql/demo/*.xml";
    public static final String BASE_PACKAGE = "com.lititi.exams.web.dao";

    @ConfigurationProperties(prefix = "ltt.mysql.datasource.exams.master")
    @Bean
    public DataSource examsMasterDataSource() {
        return new DruidDataSource();
    }

    @ConfigurationProperties(prefix = "ltt.mysql.datasource.exams.slave")
    @Bean
    public DataSource examsSlaveDataSource() {
        return new DruidDataSource();
    }

    @Bean
    public DynamicRoutingDataSource examsDynamicDataSource() {
        DynamicRoutingDataSource dynamicDataSource = new DynamicRoutingDataSource();
        Map<Object, Object> map = new HashMap<>();
        map.put(MASTER, examsMasterDataSource());
        map.put(SLAVE, examsSlaveDataSource());
        dynamicDataSource.setDefaultTargetDataSource(examsMasterDataSource());
        dynamicDataSource.setTargetDataSources(map);
        return dynamicDataSource;
    }

    @Bean
    public PlatformTransactionManager examsTransactionManager(
        @Qualifier("examsDynamicDataSource") DataSource dynamicDataSource) {

        return new DataSourceTransactionManager(dynamicDataSource);
    }

    @Bean("examsSqlSessionFactoryBean")
    public SqlSessionFactoryBean examsSqlSessionFactoryBean(
        @Qualifier("examsDynamicDataSource")DataSource dynamicDataSource) throws IOException {

        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(dynamicDataSource);
        sqlSessionFactory.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(MAPPER_XML_PATH));
        return sqlSessionFactory;
    }

    @Bean("examsSqlSessionTemplate")
    public SqlSessionTemplate examsSqlSessionTemplate(
        @Qualifier("examsSqlSessionFactoryBean") SqlSessionFactoryBean sqlSessionFactoryBean) throws Exception {
        SqlSessionFactory sqlSessionFactory = sqlSessionFactoryBean.getObject();
        if(sqlSessionFactory == null){
            throw new LttException("sqlSessionFactory 获取失败");
        }
        return new SqlSessionTemplate(sqlSessionFactory);
    }

}
