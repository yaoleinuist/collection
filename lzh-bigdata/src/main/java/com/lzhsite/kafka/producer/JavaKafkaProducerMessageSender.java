package com.lzhsite.kafka.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.Partitioner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Java实现KafkaProducer的数据发送器
 * Created by ibf on 01/11.
 */
public class JavaKafkaProducerMessageSender {
    /**
     * 具体发送消息数据的线程
     */
    public static class MessageSender implements Runnable {
        // 能够发送消息的自定义的JavaKafkaProducer对象
        private JavaKafkaProducer<String, String> producer = null;
        // 标识是否继续运行的标记对象， 当该对象值为false的时候，不继续进行运行
        private AtomicBoolean running = null;

        /**
         * 构造函数
         *
         * @param producer
         */
        public MessageSender(JavaKafkaProducer<String, String> producer, AtomicBoolean running) {
            this.producer = producer;
            this.running = running;
        }

        /**
         * 循环数据输出代码
         */
        @Override
        public void run() {
            // 获取数据发送到那一个Topic
            String topicName = this.producer.getTopicName();

            // 循环进行数据发送
            while (this.running.get()) {
                // 产生一个数据
                KeyedMessage<String, String> message = JavaKafkaProducerMessageSender.generateKeyedMessage(topicName);

                // 输出到控制台，方便进行查看
                System.out.println(message);

                // 发送数据到kafka
                this.producer.sendMessage(message);

                // 休眠一下，模拟业务
                try {
                    // 最少休眠10毫米 最多休眠99毫米
                    Thread.sleep(ThreadLocalRandom.current().nextInt(10, 100));
                } catch (InterruptedException e) {
                    // nothings
                }
            }

            // 结束处理
            System.out.println(Thread.currentThread().getName() + "完成数据输出");
        }
    }

    // 通用的随机对象
    private static ThreadLocalRandom random = ThreadLocalRandom.current();
    // 随机的原始字符串
    private static char[] charts = "qazwsxedcrfvtgbyhnujmikolp0123456789".toCharArray();
    // 原始字符串长度
    private static int chartsLength = charts.length;

    /**
     * 启动多个线程，进行数据发送操作
     *
     * @param producer   Kafka数据发送对象
     * @param pool       线程池
     * @param numThreads 线程数量
     * @param running    是否允许的标记位
     */
    public static void sendMessages(JavaKafkaProducer<String, String> producer, ExecutorService pool, int numThreads, AtomicBoolean running) {
        // 同时产生numThreads个线程
        for (int i = 0; i < numThreads; i++) {
            // 产生一个具体的Message发送者进行数据发送
            pool.submit(new MessageSender(producer, running));
        }
    }

    /**
     * 产生一个kafka的消息对象
     *
     * @param topicName 主题名称
     * @return
     */
    public static KeyedMessage<String, String> generateKeyedMessage(String topicName) {
        // 随机出key值
        String key = "key_" + random.nextInt(10, 100);
        // 随机出value中包含的单词数量,[1,4]
        int wordNums = random.nextInt(1, 5);
        // 开始随机字符串
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordNums; i++) {
            // 随机产生一个单词
            String word = generateKeyWord(random.nextInt(3, 20));
            // 累加
            sb.append(word).append(" ");
        }
        // 获取message的value的值
        String message = sb.toString().trim();

        // 创建Message对象并返回
        return new KeyedMessage<String, String>(topicName, key, message);
    }

    /**
     * 产生一个随机的字符串，长度为给定长度
     *
     * @param length
     * @return
     */
    public static String generateKeyWord(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 随机添加一个字符
            sb.append(charts[random.nextInt(chartsLength)]);
        }
        // 返回结果
        return sb.toString();
    }
}
