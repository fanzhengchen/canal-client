package com.xgn.fzc.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-15
 * Time: 5:20 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */

@Component
@Slf4j
public class CanalEventListener implements ApplicationListener<CanalEvent> {
    @Override
    public void onApplicationEvent(CanalEvent event) {
        Entry entry = event.getSource();
        log.info("{}", entry);

        CanalEntry.Header header = entry.getHeader();
        String dbName = header.getSchemaName();
        String tableName = header.getTableName();

        CanalEntry.RowChange rowChange = getRowChangeFromEntry(entry);
        log.info("dbName:{} tableName:{} {}", dbName, tableName, rowChange.getEventType());
        final CanalEntry.EventType eventType = rowChange.getEventType();
        handleRowChange(rowChange, tableName, dbName, eventType);


    }

    CanalEntry.RowChange getRowChangeFromEntry(Entry entry) {
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
    private List<String> handleRowChange(CanalEntry.RowChange rowChange, String tableName, String dbName,
                                         CanalEntry.EventType eventType) {

        List<String> commands = new ArrayList<>();

        List<CanalEntry.RowData> rowDatas = rowChange.getRowDatasList();


        for (CanalEntry.RowData rowData : rowDatas) {
            StringBuilder columnStr = new StringBuilder();
            StringBuilder valuesStr = new StringBuilder();
            boolean isFirst = true;
            List<CanalEntry.Column> columns = rowData.getAfterColumnsList();

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
                sql = String.format("UPDATE %s.%s SET (%s) = (%s) ", dbName, tableName,
                        columnStr.toString(), valuesStr.toString());
            } else if (CanalEntry.EventType.INSERT.equals(eventType)) {
                sql = String.format("INSERT INTO %s.%s (%s) VALUES (%s) WHERE %s=%S",
                        dbName, tableName, columnStr.toString(), valuesStr.toString(),
                        primaryKey, primaryKeyValue);
            }
            log.info("evenType:{}  execute sql: {}", eventType, sql);
            commands.add(sql);
        }


        return commands;
    }


}
