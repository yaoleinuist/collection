package com.lzhsite.kafka.log4j;

import org.apache.log4j.Logger;

/**
 * 测试使用Kafka的Log4jAppender将log4j的日志上传到Kafka的对应Topic中
 * Created by ibf on 01/12.
 */
public class JavaLog4jKafkaProducer {
    /**
     * 打印日志的log4j对象
     */
    private static final Logger logger = Logger.getLogger(JavaLog4jKafkaProducer.class);

    public static void main(String[] args) {
        int numRecords = 500;

        // 循环模拟产生日志数据
        for (int i = 0; i < numRecords; i++) {
            logger.debug("debug_" + i);
            logger.info("info_" + i);
            logger.warn("warn_" + i);
            logger.error("error_" + i);
        }

        // 为什么发送几条数据可能存在丢失，但是发送多条数据不存在丢失??
        // 第一个可能原因：当异步发送的时候，由于log4j日志没有达到${batch.num.messages}给定的值，没有发送出去 ===> 解决方案，自定义KafkaLog4jAppender支持参数修改
        // 第二个可能原因：当异步发送的时候，由于发送完数据就接收了，导致没有发送成功 ==> 等等一段时间
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }


    }
}
