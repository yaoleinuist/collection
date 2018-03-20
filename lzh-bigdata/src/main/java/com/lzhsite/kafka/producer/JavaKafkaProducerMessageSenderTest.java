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
     * 1、Kafka的消费并行度依赖Topic配置的分区数，如分区数为10，那么最多10台机器来并行消费（每台机器只能开启一个线程），或者一台机器消费（10个线程并行消费）。即消费并行度和分区数一致。
     * 2、（1）如果指定了某个分区,会只讲消息发到这个分区上
	 *	   （2）如果同时指定了某个分区和key,则也会将消息发送到指定分区上,key不起作用 
	 *   （3）如果没有指定分区和key,那么将会随机发送到topic的分区中
	 *	   （4）如果指定了key,那么将会以hash<key>的方式发送到分区中 
	 *	           
     * 编写一个Java的Kafka数据生产者出来，要求：
	 *   1. 支持多线程写出数据
	 *   2. 给定规则进行数据分区
	 *   3. 采用异步的方式进行数据传递
     * @throws InterruptedException
     */
    @Test
    public static void test2() throws InterruptedException {
        // 构建kafka生产者
        String topicName = "beifeng0";
        String brokerList = "hadoop.senior02:9092,hadoop.senior02:9093,hadoop.senior02:9094,hadoop.senior02:9095";
        String serilizerClassName = "kafka.serializer.StringEncoder";
        //这样就实现了kafka生产者随机分区提交数据
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
