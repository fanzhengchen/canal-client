package com.xgn.fzc.kafka;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-13
 * Time: 3:59 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Slf4j
public class CanalRowChangeDeserializer implements Deserializer<CanalEntry.RowChange> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    public CanalEntry.RowChange deserialize(String topic, byte[] data) {
        try {
            return CanalEntry.RowChange.parseFrom(data);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void close() {

    }
}
