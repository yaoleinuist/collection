package com.lzhsite.kafka.producer;

import org.junit.Test;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 测试数据发送的类代码
 * Created by ibf on 01/11.
 */
public class JavaKafkaProducerMessageSenderTest {
    public static void main(String[] args) throws InterruptedException {
//        test1();
        test2();
    }

    /**
     * 测试自定义数据分区器的JavaProducer的实现
     *
     * @throws InterruptedException
     */
    @Test
    public static void test2() throws InterruptedException {
        // 构建kafka生产者
        String topicName = "beifeng0";
        String brokerList = "hadoop.senior02:9092,hadoop.senior02:9093,hadoop.senior02:9094,hadoop.senior02:9095";
        String serilizerClassName = "kafka.serializer.StringEncoder";
        String partitionerClassName = "com.lzhsite.kafka.producer.JavaKafkaProducerePartitioner";
        JavaKafkaProducer<String, String> producer = new JavaKafkaProducer<String, String>(topicName, brokerList, serilizerClassName, partitionerClassName);

        // 多线程发送数据，构建线程数量
        int numThreads = 1;
        // 构建一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);

        // 标识是否正在运行
        AtomicBoolean running = new AtomicBoolean(true);

        // 多线程发送数据到Kafka
        JavaKafkaProducerMessageSender.sendMessages(producer, pool, numThreads, running);

        // 等待一段时间，然后进行关闭操作
        int sleepMils = 6000;
        Thread.sleep(sleepMils);

        // 更改运行标记，设置为false，表示不运行
        running.set(false);

        // 关闭kafka连接
        producer.close();

        // 关闭线程池
        pool.shutdown();
    }

    /**
     * 测试默认数据分区器的JavaProducer测试
     *
     * @throws InterruptedException
     */
    @Test
    public static void test1() throws InterruptedException {
        // 构建kafka生产者
        String topicName = "beifeng0";
        String brokerList = "hadoop.senior02:9092,hadoop.senior02:9093,hadoop.senior02:9094,hadoop.senior02:9095";
        String serilizerClassName = "kafka.serializer.StringEncoder";
        JavaKafkaProducer<String, String> producer = new JavaKafkaProducer<String, String>(topicName, brokerList, serilizerClassName);

        // 构建线程
        int numThreads = 10;
        // 构建一个线程池
        ExecutorService pool = Executors.newFixedThreadPool(numThreads);

        // 标识是否正在运行
        AtomicBoolean running = new AtomicBoolean(true);

        // 发送数据到Kafka
        JavaKafkaProducerMessageSender.sendMessages(producer, pool, numThreads, running);

        // 等待一段时间，然后进行关闭操作
        int sleepMils = 6000;
        Thread.sleep(sleepMils);

        // 标记结束数据发送操作
        running.set(false);

        // 关闭kafka连接
        producer.close();

        // 关闭线程池
        pool.shutdown();
    }
}
