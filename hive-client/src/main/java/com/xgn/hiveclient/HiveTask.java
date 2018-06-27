package com.xgn.hiveclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-27
 * Time: 4:36 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Slf4j
@Component
public class HiveTask implements ApplicationRunner {


    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("hive task");
    }


    @Autowired
    @Qualifier("hiveJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Scheduled(fixedRate = 1800_000)
    public void updateHbase() {
        log.info("update hbase");
        jdbcTemplate.execute("INSERT OVERWRITE TABLE " +
                "cms_page_h SELECT * FROM  cms_page");
    }
}
