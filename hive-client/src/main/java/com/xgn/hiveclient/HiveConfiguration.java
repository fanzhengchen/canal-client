package com.xgn.hiveclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-22
 * Time: 5:49 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Slf4j
@Configuration
public class HiveConfiguration {

    @Autowired
    private Environment env;


    @Bean(name = "hiveJdbcDataSource")
    @Qualifier("hiveJdbcDataSource")
    public DataSource provideDataSource() {
        DataSource dataSource = new DataSource();
        dataSource.setUrl(env.getProperty("hive.url"));
        dataSource.setDriverClassName(env.getProperty("hive.driver-class-name"));
        dataSource.setUsername(env.getProperty("hive.username"));
        dataSource.setPassword(env.getProperty("hive.password"));
        log.debug("hive datasource");

        return dataSource;
    }

    @Bean(name = "hiveJdbcTemplate")
    public JdbcTemplate hiveJdbcTemplate(@Qualifier("hiveJdbcDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }




}
