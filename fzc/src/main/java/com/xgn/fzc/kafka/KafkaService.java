package com.xgn.fzc.kafka;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.google.protobuf.ByteString;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-13
 * Time: 2:26 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Slf4j
@Service
public class KafkaService {


    @KafkaListener(topics = "canal")
    public void receive(ConsumerRecord<String, CanalEntry.RowChange> record) {

    }


}
