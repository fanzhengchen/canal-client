package com.xgn.fzc.config;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-20
 * Time: 3:08 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Configuration
@EnableConfigurationProperties(CanalProperties.class)
public class CanalConfiguration {

    @Autowired
    CanalProperties canalProperties;

    @Bean
    public CanalConnector canalConnector() {
        return CanalConnectors.newSingleConnector(
                new InetSocketAddress(canalProperties.getServer(), canalProperties.getPort()),
                canalProperties.getDestination(),
                canalProperties.getDestination(),
                canalProperties.getPassword());
    }
}
