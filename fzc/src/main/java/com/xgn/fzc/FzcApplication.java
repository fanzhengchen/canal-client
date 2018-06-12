package com.xgn.fzc;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.InetSocketAddress;

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
