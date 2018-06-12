package com.xgn.fzc;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-12
 * Time: 4:03 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Slf4j
@Component
public class CanalRunner implements ApplicationRunner {

    @Autowired
    CanalService canalService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        canalService.start();
    }
}
