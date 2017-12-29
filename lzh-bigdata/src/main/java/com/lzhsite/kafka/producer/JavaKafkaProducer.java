package com.lzhsite.kafka.producer;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Properties;

/**
 * 使用Java代码实现的一个Kafka数据生成者，可以通过该类发送消息数据
 * Created by ibf on 01/11.
 *
 * @param <KEY>   指定发送数据的key类型
 * @param <VALUE> 指定发送数据的value类型
 */
public class JavaKafkaProducer<KEY, VALUE> {
    private static final Logger logger = Logger.getLogger(JavaKafkaProducer.class);

    // 给定消息发到到topic的名称
    private String topicName;
    // 指定kafka连接的监听ip地址和端口号
    private String brokerList;
    // 指定producer等等kafka返回的结果数
    private int acks = 0;
    // 指定数据发送方式是否是同步
    private boolean isSync = true;
    // 指定消息的序列化类全称
    private String kafkaSerializerClassName = "kafka.serializer.DefaultEncoder";
    // 指定消息key的虚拟化的类全称
    private String kafkaKeySerializerClassName = kafkaSerializerClassName;
    // 指定根据key进行数据分区的类全称
    private String kafkaPartitionerClass = "kafka.producer.DefaultPartitioner";

    // 连接kafka集群的producer类
    private Producer<KEY, VALUE> producer = null;

    /**
     * 最小参数构造函数
     *
     * @param topicName  给定发送数据的Topic名称
     * @param brokerList 给定连接Kafka的服务器连接参数
     */
    public JavaKafkaProducer(String topicName, String brokerList) {
        this(topicName, brokerList, "kafka.serializer.DefaultEncoder", "kafka.producer.DefaultPartitioner");
    }

    /**
     * 构造函数
     *
     * @param topicName           给定发送数据的Topic名称
     * @param brokerList          给定连接Kafka的服务器连接参数
     * @param serializerClassName 给定序列化类，默认DefaultEncoder在传递String的时候存在Debug
     */
    public JavaKafkaProducer(String topicName, String brokerList, String serializerClassName) {
        this(topicName, brokerList, serializerClassName, "kafka.producer.DefaultPartitioner");
    }

    /**
     * 构造函数
     *
     * @param topicName             给定发送数据的Topic名称
     * @param brokerList            给定连接Kafka的服务器连接参数
     * @param serializerClassName   给定序列化类
     * @param kafkaPartitionerClass 给定指定数据分区器类
     */
    public JavaKafkaProducer(String topicName, String brokerList, String serializerClassName, String kafkaPartitionerClass) {
        this.topicName = topicName;
        this.brokerList = brokerList;
        this.kafkaKeySerializerClassName = serializerClassName;
        this.kafkaSerializerClassName = serializerClassName;
        this.kafkaPartitionerClass = kafkaPartitionerClass;
        // 进行初始化操作
        this.initialJavaProducer();
    }

    /**
     * 构造函数
     *
     * @param topicName                   给定发送数据的Topic名称
     * @param brokerList                  给定连接Kafka的服务器连接参数
     * @param acks                        等等KafkaServer返回的结果值
     * @param isSync                      是否同步发送数据
     * @param kafkaSerializerClassName    给定value的序列化类
     * @param kafkaKeySerializerClassName 给定key的序列化类
     * @param kafkaPartitionerClass       给定指定数据分区器类
     */
    public JavaKafkaProducer(String topicName, String brokerList, int acks, boolean isSync, String kafkaSerializerClassName, String kafkaKeySerializerClassName, String kafkaPartitionerClass) {
        this.topicName = topicName;
        this.brokerList = brokerList;
        if (acks == 0 || acks == 1 || acks == -1) {
            // acks的参数只支持-1,0,1
            this.acks = acks;
        }
        this.isSync = isSync;
        this.kafkaSerializerClassName = kafkaSerializerClassName;
        this.kafkaKeySerializerClassName = kafkaKeySerializerClassName;
        this.kafkaPartitionerClass = kafkaPartitionerClass;

        // 进行初始化操作
        this.initialJavaProducer();
    }

    /**
     * 进行连接kafka集群的producer初始化操作， 构建一个Producer对象
     */
    private void initialJavaProducer() {
        // TODO: 假设所有参数正确，所以不进行任何参数的过滤判断
        Properties props = new Properties();
        // 给定kafka连接参数，其实就是kafka的server.properties配置文件中的host.name和port参数
        props.put("metadata.broker.list", this.brokerList);
        // 给定producer是否等待结果返回
        props.put("request.required.acks", String.valueOf(this.acks));
        // 设置数据发送方式是异步还是同步
        if (this.isSync) {
            // 同步发送数据，默认
            props.put("producer.type", "sync");
        } else {
            // 异步发送数据
            props.put("producer.type", "async");
        }
        // 设置数据序列化的类
        props.put("serializer.class", this.kafkaSerializerClassName);
        props.put("key.serializer.class", this.kafkaKeySerializerClassName);
        // 设置分区类
        props.put("partitioner.class", this.kafkaPartitionerClass);

        // 设置一些额外参数
        props.put("message.send.max.retries", "3"); // 重试次数
        props.put("batch.num.messages", "200"); // 批量提交的消息数量
        props.put("send.buffer.bytes", "102400"); // 缓存区大小，默认10KB

        // 创建Kafka中的生产者配置信息对象
        ProducerConfig config = new ProducerConfig(props);

        // 构建Kafka的生产者对象
        this.producer = new Producer<KEY, VALUE>(config);
    }

    /**
     * 获取主题名称
     *
     * @return
     */
    public String getTopicName() {
        return this.topicName;
    }

    /**
     * 发送一条message数据
     *
     * @param message
     */
    public void sendMessage(KeyedMessage<KEY, VALUE> message) {
        this.producer.send(message);
    }

    /**
     * 发送多条消息数据
     *
     * @param messages
     */
    public void sendMessages(List<KeyedMessage<KEY, VALUE>> messages) {
        this.producer.send(messages);
    }

    /**
     * 关闭kafka的producer连接
     */
    public void close() {
        this.producer.close();
    }
}
