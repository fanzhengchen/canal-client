package com.xgn.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-22
 * Time: 11:36 AM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
public class EntryDeserializer implements Deserializer<CanalEntry.Entry> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public CanalEntry.Entry deserialize(String topic, byte[] data) {
        try {
            return CanalEntry.Entry.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }
}
