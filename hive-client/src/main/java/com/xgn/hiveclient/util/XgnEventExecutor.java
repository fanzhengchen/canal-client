package com.xgn.hiveclient.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import sun.jvm.hotspot.utilities.Assert;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-07-02
 * Time: 3:34 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Slf4j
@Component
public class XgnEventExecutor implements Executor {

    private Thread thread;
    private BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(Integer.MAX_VALUE);


    private boolean isInEventLoop() {
        return thread == Thread.currentThread();
    }

    private boolean isStarted() {
        return thread != null && thread.getState() != Thread.State.TERMINATED;
    }

    private void doStart() {
        Assert.that(thread == null, "thread shall be null");

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("xgn event executor run");
                for (; ; ) {
                    try {
                        Optional.of(queue.take())
                                .ifPresent(runnable -> runnable.run());
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                }
            }
        });
        thread.setName(this.getClass().getName() + " thread");
        log.info("before thread start");
        thread.start();
    }


    @Override
    public void execute(Runnable command) {
        if (!isStarted()) {
            doStart();
        }
        if (!queue.offer(command)) {
            log.error("command runnable cannot be added to task list");
        }
    }
}
