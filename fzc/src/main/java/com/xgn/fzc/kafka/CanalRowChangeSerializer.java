package com.xgn.fzc.kafka;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-13
 * Time: 3:58 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
public class CanalRowChangeSerializer implements Serializer<CanalEntry.RowChange> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, CanalEntry.RowChange data) {
        return data.toByteArray();
    }

    @Override
    public void close() {

    }
}
