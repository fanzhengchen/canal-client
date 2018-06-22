package com.xgn.fzc.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.google.protobuf.InvalidProtocolBufferException;
import com.xgn.fzc.config.CanalProperties;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.SuccessCallback;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;


/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-12
 * Time: 4:13 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Slf4j
@Service
public class CanalService implements ApplicationContextAware {

    protected static final String SEP = SystemUtils.LINE_SEPARATOR;

    protected static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Autowired
    private CanalConnector connector;

    @Autowired
    CanalProperties canalProperties;

    @Resource
    KafkaTemplate kafkaTemplate;

    private Thread thread;

    private volatile boolean running;

    private ApplicationContext applicationContext;

    protected static String context_format = null;
    protected static String row_format = null;
    protected static String transaction_format = null;


    static {
        context_format = SEP + "****************************************************" + SEP;
        context_format += "* Batch Id: [{}] ,count : [{}] , memsize : [{}] , Time : {}" + SEP;
        context_format += "* Start : [{}] " + SEP;
        context_format += "* End : [{}] " + SEP;
        context_format += "****************************************************" + SEP;

        row_format = SEP
                + "----------------> binlog[{}:{}] , name[{},{}] , eventType : {} , executeTime : {}({}) , delay : {} ms"
                + SEP;

        transaction_format = SEP + "================> binlog[{}:{}] , executeTime : {}({}) , delay : {}ms" + SEP;

    }

    private Thread.UncaughtExceptionHandler handler =
            new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    log.error("uncaught exception {} {}", t, e);
                }
            };

    public void start() {
        running = false;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                process();
                log.error("after process");
            }
        });


        log.info("canal start thread {}", thread);

        thread.setUncaughtExceptionHandler(handler);
        thread.start();
        running = true;

    }

    /**
     * canal 停止了
     */
    public void stop() {
        if (!running) {
            return;
        }
        running = false;
        if (thread != null) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void process() {
        int blockSize = 5 << 10;
        log.info("canal client start");
        while (running) {
            try {
                MDC.put("destination", canalProperties.getDestination());
                connector.connect();
                connector.subscribe();
                connector.rollback();
                while (running) {
                    Message message = connector.getWithoutAck(blockSize);
                    long batchId = message.getId();
                    int size = message.getEntries().size();
                    if (batchId == -1 || size == 0) {

                    } else {
                        printSummary(message, batchId, size);
                        printEntries(message.getEntries());
                    }
                    /**
                     * 提交确认
                     */
                    connector.ack(batchId);
                }
            } finally {
                connector.disconnect();
            }
        }
    }

    private void printSummary(Message message, long batchId, int size) {
        long memsize = 0;
        for (CanalEntry.Entry entry : message.getEntries()) {

            //entry.toByteArray();


            memsize += entry.getHeader().getEventLength();
        }

        String startPosition = null;
        String endPosition = null;
        if (!CollectionUtils.isEmpty(message.getEntries())) {
            startPosition = buildPositionForDump(message.getEntries().get(0));
            endPosition = buildPositionForDump(message.getEntries().get(message.getEntries().size() - 1));
        }

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        log.info(context_format, new Object[]{batchId, size, memsize, format.format(new Date()), startPosition,
                endPosition});
    }

    protected String buildPositionForDump(CanalEntry.Entry entry) {
        long time = entry.getHeader().getExecuteTime();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        return entry.getHeader().getLogfileName() + ":" + entry.getHeader().getLogfileOffset() + ":"
                + entry.getHeader().getExecuteTime() + "(" + format.format(date) + ")";
    }

    private void printEntries(List<CanalEntry.Entry> entries) {
        for (CanalEntry.Entry entry : entries) {

            long executeTime = entry.getHeader().getExecuteTime();
            long delayTime = System.currentTimeMillis() - executeTime;
            Date date = new Date(entry.getHeader().getExecuteTime());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN || entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONBEGIN) {
                    CanalEntry.TransactionBegin begin = null;
                    try {
                        begin = CanalEntry.TransactionBegin.parseFrom(entry.getStoreValue());
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
                    }
                    // 打印事务头信息，执行的线程id，事务耗时
                    log.info(transaction_format,
                            new Object[]{entry.getHeader().getLogfileName(),
                                    String.valueOf(entry.getHeader().getLogfileOffset()),
                                    String.valueOf(entry.getHeader().getExecuteTime()), simpleDateFormat.format(date),
                                    String.valueOf(delayTime)});
                    log.info(" BEGIN ----> Thread id: {}", begin.getThreadId());
                } else if (entry.getEntryType() == CanalEntry.EntryType.TRANSACTIONEND) {
                    CanalEntry.TransactionEnd end = null;
                    try {
                        end = CanalEntry.TransactionEnd.parseFrom(entry.getStoreValue());
                    } catch (InvalidProtocolBufferException e) {
                        throw new RuntimeException("parse event has an error , data:" + entry.toString(), e);
                    }
                    // 打印事务提交信息，事务id
                    log.info("----------------\n");
                    log.info(" END ----> transaction id: {}", end.getTransactionId());
                    log.info(transaction_format,
                            new Object[]{entry.getHeader().getLogfileName(),
                                    String.valueOf(entry.getHeader().getLogfileOffset()),
                                    String.valueOf(entry.getHeader().getExecuteTime()), simpleDateFormat.format(date),
                                    String.valueOf(delayTime)});
                }

                continue;
            }

            if (entry.getEntryType() == CanalEntry.EntryType.ROWDATA) {
                Optional.ofNullable(applicationContext)
                        .ifPresent(context -> {
                            log.info("push event {} {}", entry, context);
                            context.publishEvent(new CanalEvent(entry));
                        });

            }
        }
    }


    public boolean isRunning() {
        return running;
    }

    @PostConstruct
    public void onDestroy() {
        stop();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
