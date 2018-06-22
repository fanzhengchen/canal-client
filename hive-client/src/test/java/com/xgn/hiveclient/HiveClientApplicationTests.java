package com.xgn.hiveclient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HiveClientApplicationTests {


    @Autowired
    @Qualifier("hiveJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testHiveJdbcTemplate() {
        jdbcTemplate.execute("CREATE TABLE hive_t (foo INT, bar STRING) " +
                "clustered by (foo) into 2 buckets stored as " +
                "orc TBLPROPERTIES('transactional'='true')  ");
    }

    @Test
    public void testHiveCreateTable() {

        jdbcTemplate.execute("CREATE TABLE cms_page (" +
                "page_id STRING, " +
                "page_name STRING, " +
                "status STRING, " +
                "create_time TIMESTAMP, " +
                "min_version INT," +
                "type STRING, " +
                "platform STRING, " +
                "editor STRING, " +
                "create_by STRING, " +
                "edit_time TIMESTAMP, " +
                "project_id STRING, " +
                "page_info STRING, " +
                "light_page_info STRING, " +
                "shop_id STRING, " +
                "page_url STRING, " +
                "page_online_type STRING, " +
                "page_online_start STRING, " +
                "page_online_end STRING, " +
                "page_jump_link STRING, " +
                "copy_write STRING, " +
                "share_link STRING, " +
                "is_delete INT )" +
                "clustered by (page_id) into 2 buckets stored as " +
                "orc TBLPROPERTIES('transactional'='true')  ");
    }

    @Test
    public void insertHive() {

    }


}
