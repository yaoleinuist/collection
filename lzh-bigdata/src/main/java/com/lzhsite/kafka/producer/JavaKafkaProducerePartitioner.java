package com.lzhsite.kafka.producer;

/**
 * Created by ibf on 01/11.
 */

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * Kafka Producer自定义数据分区器
 * java.lang.NoSuchMethodException: com.ibeifeng.senior.kafka.producer.JavaKafkaProducerePartitioner.<init>(kafka.utils.VerifiableProperties)
 * ===> 没有这样的构造方法，必须给定
 */
public class JavaKafkaProducerePartitioner implements Partitioner {

    /**
     * 该构造函数必须给定，因为Kafka底层对调用该构造函数进行初始化操作
     *
     * @param properties
     */
    public JavaKafkaProducerePartitioner(VerifiableProperties properties) {
        // nothings
    }

    @Override
    public int partition(Object key, int numPartitions) {
        // TODO: 根据自己的数据类型特征进行数据分区操作，这里采用key中间的数字进行分区
        // TODO: key是发送消息的KEY部分，numPartitions是对应Topic的Partition数量
        int num = Integer.valueOf(((String) key).replaceAll("key_", "").trim());
        return num % numPartitions;
    }
}
