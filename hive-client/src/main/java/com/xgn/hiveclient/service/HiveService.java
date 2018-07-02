package com.xgn.hiveclient.service;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.xgn.hiveclient.util.XgnEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-22
 * Time: 3:09 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Slf4j
@Service
public class HiveService implements ApplicationRunner {


    @Autowired
    @Qualifier("hiveJdbcTemplate")
    JdbcTemplate jdbcTemplate;

    @Autowired
    XgnEventExecutor eventExecutor;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Hive Service la");


    }

    @KafkaListener(topics = {"ca"})
    public void receiveKafkaTopics(ConsumerRecord<?, CanalEntry.Entry> record,
                                   Acknowledgment ack) {
        log.info("receive kafka message {} {}", record, ack);

        record.headers();
        CanalEntry.Entry entry = record.value();


        CanalEntry.Header header = entry.getHeader();
        String dbName = header.getSchemaName();
        String tableName = header.getTableName();

        CanalEntry.RowChange rowChange = getRowChangeFromEntry(entry);
        log.info("dbName:{} tableName:{} {}", dbName, tableName, rowChange.getEventType());
        final CanalEntry.EventType eventType = rowChange.getEventType();
        /**
         * 处理canal entry 并将数据同步到目标数据库
         */
        eventExecutor.execute(new Runnable() {
            @Override
            public void run() {
                handleRowChange(rowChange, tableName, dbName, eventType);
                ack.acknowledge();
            }
        });


    }

    CanalEntry.RowChange getRowChangeFromEntry(CanalEntry.Entry entry) {
        try {
            return CanalEntry.RowChange.parseFrom(entry.getStoreValue());
        } catch (Exception e) {
            throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
        }
    }


    /**
     * 转换为 sql 语句
     *
     * @param rowChange
     * @param tableName
     * @param dbName
     * @param eventType
     * @return
     */
    private void handleRowChange(CanalEntry.RowChange rowChange, String tableName, String dbName,
                                 CanalEntry.EventType eventType) {

        List<String> commands = new ArrayList<>();

        List<CanalEntry.RowData> rowDatas = rowChange.getRowDatasList();


        log.info("handle row change");
        for (CanalEntry.RowData rowData : rowDatas) {

            boolean isFirst = true;
            List<CanalEntry.Column> columns = null;
            if (CanalEntry.EventType.DELETE.equals(eventType)) {
                columns = rowData.getBeforeColumnsList();
            } else {
                columns = rowData.getAfterColumnsList();
            }

            String primaryKey = null;
            String primaryKeyValue = "";
            for (CanalEntry.Column column : columns) {
                if (column.getIsKey()) {
                    primaryKey = column.getName();
                    primaryKeyValue = column.getValue();
                    break;
                }
            }

            String sql = "";
            if (CanalEntry.EventType.UPDATE.equals(eventType)) {
                StringBuilder updateStr = new StringBuilder();
                isFirst = true;
                for (CanalEntry.Column column : columns) {
                    if (column.hasUpdated() && !column.getIsNull() && !column.getIsKey()) {
                        if (!isFirst) {
                            updateStr.append(",");
                        }
                        updateStr.append(column.getName());
                        updateStr.append("=");
                        updateStr.append("'");
                        updateStr.append(column.getValue());
                        updateStr.append("'");
                        isFirst = false;
                    }
                }
                sql = String.format("UPDATE %s.%s SET %s WHERE %s='%s'", dbName, tableName,
                        updateStr, primaryKey, primaryKeyValue);

                log.info("update sql {}", sql);
                jdbcTemplate.execute(sql);

            } else if (CanalEntry.EventType.INSERT.equals(eventType)) {
                StringBuilder columnStr = new StringBuilder();
                StringBuilder valuesStr = new StringBuilder();
                isFirst = true;
                for (CanalEntry.Column column : columns) {

                    if (!column.getIsNull()) {
                        if (!isFirst) {
                            columnStr.append(",");
                            valuesStr.append(",");
                        }
                        isFirst = false;
                        columnStr.append(column.getName());
                        valuesStr.append("'");
                        valuesStr.append(column.getValue());
                        valuesStr.append("'");
                    }
                }
                sql = String.format("INSERT INTO TABLE %s.%s (%s) VALUES (%s)",
                        dbName, tableName, columnStr.toString(), valuesStr.toString());
                log.info("insert sql: {}", sql);

                jdbcTemplate.execute(sql);

            } else if (CanalEntry.EventType.DELETE.equals(eventType)) {
                sql = String.format("DELETE FROM %s.%s WHERE %s='%s'", dbName, tableName,
                        primaryKey, primaryKeyValue);


            }
            log.info("evenType:{}  execute sql: {}", eventType, sql);


        }


    }

}
