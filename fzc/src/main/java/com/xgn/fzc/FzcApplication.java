package com.xgn.fzc;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Slf4j
@EnableScheduling
@SpringBootApplication
public class FzcApplication {

    public static void main(String[] args) {
        SpringApplication.run(FzcApplication.class, args);
    }


    @Bean
    public CanalConnector canalConnector() {
        return CanalConnectors.newSingleConnector(
                new InetSocketAddress("172.16.7.87", 11111), "example", "",
                "");
    }



}
