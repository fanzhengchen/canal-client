package com.xgn.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-22
 * Time: 11:35 AM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
public class EntrySerializer implements Serializer<CanalEntry.Entry> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public byte[] serialize(String topic, CanalEntry.Entry data) {
        return data.toByteArray();
    }

    @Override
    public void close() {

    }
}
