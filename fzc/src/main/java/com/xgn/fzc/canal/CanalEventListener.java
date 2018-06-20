package com.xgn.fzc.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import com.xgn.fzc.mapper.SqlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
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


    @Autowired
    SqlMapper sqlMapper;


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
        /**
         * 处理canal entry 并将数据同步到目标数据库
         */
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
                    if (column.hasUpdated() && !column.getIsNull()) {
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
                sql = String.format("UPDATE %s.%s SET %s WHERE %s='%s';", dbName, tableName,
                        updateStr, primaryKey, primaryKeyValue);
                sqlMapper.update(sql);

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
                sql = String.format("INSERT INTO %s.%s (%s) VALUES (%s);",
                        dbName, tableName, columnStr.toString(), valuesStr.toString());
                log.info("insert sql: {}", sql);
                sqlMapper.insert(sql);

            } else if (CanalEntry.EventType.DELETE.equals(eventType)) {
                sql = String.format("DELETE FROM %s.%s WHERE %s='%s';", dbName, tableName,
                        primaryKey, primaryKeyValue);

                sqlMapper.delete(sql);
            }
            log.info("evenType:{}  execute sql: {}", eventType, sql);

            commands.add(sql);
        }

        return commands;
    }


}
