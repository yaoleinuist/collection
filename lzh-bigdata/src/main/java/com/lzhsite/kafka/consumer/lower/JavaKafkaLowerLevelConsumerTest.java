package com.lzhsite.kafka.consumer.lower;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by ibf on 01/11.
 */
public class JavaKafkaLowerLevelConsumerTest {
    public static void main(String[] args) throws InterruptedException {
        test1();
    }

    public static void test1() throws InterruptedException {
        JavaKafkaLowerLevelConsumer consumer = new JavaKafkaLowerLevelConsumer();

        List<KafkaBrokerInfo> brokers = new ArrayList<>();
        brokers.add(new KafkaBrokerInfo("hadoop.senior02", 9092));
/*        brokers.add(new KafkaBrokerInfo("hadoop.senior02", 9093));
        brokers.add(new KafkaBrokerInfo("hadoop.senior02", 9094));
        brokers.add(new KafkaBrokerInfo("hadoop.senior02", 9095));*/
        String topic = "beifeng0";
        int partitionID = 4;
        AtomicBoolean running = new AtomicBoolean(true);


        consumer.processMessages(brokers, topic, partitionID, running);


        // 等待一段时间关闭
        Thread.sleep(100000);

        running.set(false);
    }
}
