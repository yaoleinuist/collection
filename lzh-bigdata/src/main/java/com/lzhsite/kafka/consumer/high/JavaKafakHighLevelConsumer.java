package com.lzhsite.kafka.consumer.high;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.serializer.StringDecoder;
import kafka.utils.VerifiableProperties;

/**
 * 使用高级别的KafkaConsumerAPI测试类
 * Created by ibf on 01/11.
 */
public class JavaKafakHighLevelConsumer {
    /**
     * kafka的数据消费对象
     * ConsumerConnector connector = Consumer.create(consumerConfig);
     */
    private ConsumerConnector connector = null;

    /**
     * 线程池
     */
    private ExecutorService executorPool;

    /**
     * 构造函数
     *
     * @param groupId   Consumer所属group.id
     * @param zookeeper Kafka元数据所在的zk的连接url
     */
    public JavaKafakHighLevelConsumer(String groupId, String zookeeper) {
        // 根据传入的数据构建一个连接kafka的消费者对象
        this.connector = this.createConsumerConnector(groupId, zookeeper);
        // 创建一个线程池
        this.executorPool = Executors.newCachedThreadPool();
    }

    /**
     * 消费Kafka数据
     *
     * @param topicCountMap 指定对应的topic
     */
    public void consumerKafkaMessages(Map<String, Integer> topicCountMap) {
        // 指定数据的解码器
        StringDecoder keyDecoder = new StringDecoder(new VerifiableProperties());
        StringDecoder valueDecoder = keyDecoder;

        // 获取给定TOPIC的流式处理器迭代集合
        Map<String, List<KafkaStream<String, String>>> consumerTopicStreams =
                this.connector.createMessageStreams(topicCountMap, keyDecoder, valueDecoder);

        // 对流式数据迭代器集群进行操作处理 => 模拟业务：打印一下数据
        // 每个KafkaStream最好有一个线程来进行处理
        // 对KafkaStream流进行处理操作
        for (Map.Entry<String, List<KafkaStream<String, String>>> entry : consumerTopicStreams.entrySet()) {
            // 获取topic名称
            String topicName = entry.getKey();

            // 处理该topic的数据流对象
            int seqNo = 0;
            for (KafkaStream<String, String> stream : entry.getValue()) {
                // 直接将对象放到一个线程中去执行即可
                this.executorPool.submit(
                        new ConsumerKafkaStreamProcesser(stream, seqNo, topicName));
                seqNo++;
            }
        }
    }

    /**
     * 停止kafka数据的消费
     */
    public void shutdown() {
        // 关闭和kafka的连接，这个操作会导致iter.hasNext返回false
        if (this.connector != null) {
            this.connector.shutdown();
        }

        // 关闭线程池
        if (this.executorPool != null) {
            // 2.1 关闭线程池
            this.executorPool.shutdown();

            // 2.2 等待关闭完成，等待5s
            try {
                if (!this.executorPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.out.println("Timed out waiting for consumer threads to shutdown, exiting uncleanly!");
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupted for consumer threads to shutdown, exiting uncleanly!");
            }
        }
    }

    /**
     * 根据传入的参数构建一个Kafka的ConsumerConnector连接对象
     *
     * @param groupId
     * @param zookeeper
     * @return
     */
    private ConsumerConnector createConsumerConnector(String groupId, String zookeeper) {
        // 第一步：构建参数
        Properties props = new Properties();
        // 指定当前创建的consumer所属组是那个
        props.put("group.id", groupId);
        // 指定当前连接的kafka的zk连接信息是那个
        props.put("zookeeper.connect", zookeeper);
        // 更改offset提交zk的间隔时间
        props.put("auto.commit.interval.ms", "1000");
        ConsumerConfig config = new ConsumerConfig(props);

        // 第二个：根据Consumer的上下文参数，构建连接对象
        return Consumer.createJavaConsumerConnector(config);
    }

    /**
     * 具体Kafka流对象处理线程
     */
    public static class ConsumerKafkaStreamProcesser implements Runnable {
        // 该线程负责处理的kafka流对象
        private KafkaStream<String, String> stream;
        // 序列号
        private int seqNo;
        // topic名称
        private String topicName;

        /**
         * 构造函数
         *
         * @param stream
         * @param seqNo
         * @param topicName
         */
        public ConsumerKafkaStreamProcesser(KafkaStream<String, String> stream, int seqNo, String topicName) {
            this.stream = stream;
            this.seqNo = seqNo;
            this.topicName = topicName;
        }

        @Override
        public void run() {
            // 1. 将stream流转换为一个迭代器对象
            ConsumerIterator<String, String> iter = stream.iterator();
            // 2. 迭代器数据处理
            while (iter.hasNext()) {
                // 2.1 获取迭代器中的数据
                MessageAndMetadata<String, String> mam = iter.next();
                // 2.2 进行数据输出，MessageAndMetadata包含了偏移量等相关信息
                System.out.println("Topic:" + this.topicName + ", seq:" + this.seqNo + ", key:" + mam.key() + ", value:" + mam.message() + ", offset:" + mam.offset() + ", partition:" + mam.partition());
            }
        }
    }
}
