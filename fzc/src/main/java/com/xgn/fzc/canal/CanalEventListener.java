package com.xgn.fzc.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-15
 * Time: 5:20 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */

@Component
@Slf4j
public class CanalEventListener implements ApplicationListener<CanalEvent> {
    @Override
    public void onApplicationEvent(CanalEvent event) {
        Entry entry = event.getSource();
        log.info(" {}",entry);
    }


    @EventListener(CanalEvent.class)
    public void handleUpdateEvent(CanalEvent event) {

    }
}
