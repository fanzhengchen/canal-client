package com.xgn.fzc.canal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import org.springframework.context.ApplicationEvent;
import com.alibaba.otter.canal.protocol.CanalEntry.Entry;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-15
 * Time: 5:20 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
public class CanalEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param entry the object on which the event initially occurred (never {@code null})
     */
    public CanalEvent(Entry entry) {
        super(entry);
    }


    @Override
    public Entry getSource() {
        return (Entry) super.getSource();
    }
}
