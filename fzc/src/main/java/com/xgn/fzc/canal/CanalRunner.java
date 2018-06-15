package com.xgn.fzc.canal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

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
