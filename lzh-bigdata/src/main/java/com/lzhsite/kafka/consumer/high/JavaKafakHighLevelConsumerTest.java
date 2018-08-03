package com.lzhsite.kafka.consumer.high;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka Java High Level Consumer Test类
 * Created by ibf on 01/11.
 */
public class JavaKafakHighLevelConsumerTest {
    public static void main(String[] args) throws InterruptedException {
        test1();
    }

    public static void test1() throws InterruptedException {
        // 根据给定的groupid和zk创建kafka的数据消费对象
        String groupId = "java01";
        String zookeeper = "localhost:2181/kafka";//"hadoop.senior02:2181/kafka";
        JavaKafakHighLevelConsumer consumer = new JavaKafakHighLevelConsumer(groupId, zookeeper);

        // 创建需要消费的kafka的topic
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put("beifeng0", 10);
        topicCountMap.put("beifeng3", 1);
        topicCountMap.put("test", 1);

        // 消费数据
        consumer.consumerKafkaMessages(topicCountMap);

        // 等等一段时间后关闭连接
        long sleepMills = 60000;
        Thread.sleep(sleepMills);

        // 关闭
        consumer.shutdown();
    }
}
