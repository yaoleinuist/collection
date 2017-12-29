package com.lzhsite.kafka.consumer.lower;

import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.*;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.message.MessageAndOffset;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Kafka Lower consumer API ==> Simple Consumer API<br/>
 * Created by ibf on 01/11.
 */
public class JavaKafkaLowerLevelConsumer {
    /**
     * 临时保存的备份broker信息
     */
    private List<KafkaBrokerInfo> replicaBrokers = new ArrayList<KafkaBrokerInfo>();

    /**
     * 对给定topic的给定partition进行消息数据的处理
     *
     * @param brokers     Kafka连接信息
     * @param topic       主题名称
     * @param partitionID 分区id，从0开始
     * @param running     标记是否继续运行
     */
    public void processMessages(List<KafkaBrokerInfo> brokers, String topic, int partitionID, AtomicBoolean running) {
        // 获取leader对象
        PartitionMetadata metadata = this.findLeader(brokers, topic, partitionID);

        // 过滤判断
        if (metadata == null) {
            throw new IllegalArgumentException("没法找到主broker节点");
        }
        if (metadata.leader() == null) {
            throw new IllegalArgumentException("找到的元数据中不包含主节点");
        }

        // 根据metadata数据获取leader所在的broker相关信息
        String leaderHost = metadata.leader().host();
        int leaderPort = metadata.leader().port();
        String clientName = "client_" + topic + "_" + partitionID;
        String groupID = clientName;

        // 连接leader
        SimpleConsumer consumer = new SimpleConsumer(leaderHost, leaderPort, 10000, 64 * 1024, clientName);

        // 先获取偏移量,先从kafka中获取，然后获取该parition的最早偏移量
        long readOffset = this.getLastOffset(consumer, groupID, topic, partitionID, clientName, kafka.api.OffsetRequest.EarliestTime());

        // 获取数据
        int numErrors = 0;
        while (running.get()) {
            try {
                // 构建数据请求参数
                kafka.api.FetchRequest request = new FetchRequestBuilder()
                        .clientId(clientName)
                        .addFetch(topic, partitionID, readOffset, 10000)
                        .build();

                // 从kafka获取数据
                FetchResponse response = consumer.fetch(request);

                //数据处理
                if (response.hasError()) {
                    if (numErrors > 5) {
                        // 当异常连续超过5次的时候，直接结束循环
                        break;
                    }

                    // 获取一次code
                    short code = response.errorCode(topic, partitionID);
                    System.out.println("Error fetching data from the Broker:" + leaderHost + ", Reason:" + code);

                    // 当code值是offset范围不对的时候，更新之
                    if (code == ErrorMapping.OffsetOutOfRangeCode()) {
                        // offset超过范围
                        readOffset = this.getLastOffset(consumer, groupID, topic, partitionID, clientName, kafka.api.OffsetRequest.EarliestTime());
                        continue;
                    }

                    // 考虑是否是consumer的异常；是否是leader切换，重新创建一个leader对象
                    consumer.close();
                    consumer = null;

                    // 重新创建，考虑主节点变化的情况
                    consumer = this.findNewLeader(leaderHost, topic, partitionID, clientName);
                    continue;
                }
                // 重置error数量
                numErrors = 0;

                // 正常情况数据读取
                ByteBufferMessageSet bbms = response.messageSet(topic, partitionID);
                for (MessageAndOffset messageAndOffset : bbms) {
                    long currentOffset = messageAndOffset.offset();
                    if (currentOffset < readOffset) {
                        System.out.println("Found and old offset:" + currentOffset + ", Expection:" + readOffset);
                        continue;
                    }

                    // 更新一下个需要开始读取数据的offset
                    readOffset = messageAndOffset.nextOffset();

                    // 获取数据, 不获取key，只获取value
                    ByteBuffer payload = messageAndOffset.message().payload();

                    byte[] bytes = new byte[payload.limit()];
                    payload.get(bytes);
                    System.out.println(currentOffset + ":" + new String(bytes, "UTF-8"));

                    // TODO: 如果需要可以读一条数据继续一个offset的修改
                }

                // TODO: 修改偏移量
                Map<TopicAndPartition, OffsetAndMetadata> requestInfo = new HashMap<TopicAndPartition, OffsetAndMetadata>();
                requestInfo.put(new TopicAndPartition(topic, partitionID), new OffsetAndMetadata(readOffset, OffsetAndMetadata.NoMetadata(), -1));
                OffsetCommitRequest ocRequest = new OffsetCommitRequest(groupID, requestInfo, 0, clientName);
                consumer.commitOffsets(ocRequest);

            } catch (Exception e) {
                if (numErrors > 5) {
                    break;
                }
                // 考虑是否是consumer的异常
                consumer.close();
                consumer = null;

                // 重新创建，考虑主节点变化的情况
                consumer = this.findNewLeader(leaderHost, topic, partitionID, clientName);
                continue;
            }
        }

        if (numErrors > 0) {
            throw new IllegalArgumentException("数据处理的时候有异常");
        }

        // 关闭操作
        if (consumer != null) {
            consumer.close();
        }
    }

    /**
     * 获取一个新的leader对象
     *
     * @param leaderHost
     * @param topic
     * @param partitionID
     * @param clientName
     * @return
     */
    private SimpleConsumer findNewLeader(String leaderHost, String topic, int partitionID, String clientName) {
        for (int i = 0; i < 3; i++) {
            boolean gotoSleep = false;
            // 循环3次获取broker的值
            PartitionMetadata metadata = this.findLeader(this.replicaBrokers, topic, partitionID);
            if (metadata == null) {
                gotoSleep = true;
            } else if (metadata.leader() == null) {
                gotoSleep = true;
            } else if (leaderHost.equalsIgnoreCase(metadata.leader().host()) && i == 0) {
                // 表示正在切换过程中
                gotoSleep = true;
            } else {
                String host = metadata.leader().host();
                int port = metadata.leader().port();
                return new SimpleConsumer(host, port, 10000, 64 * 1024, clientName);
            }

            if (gotoSleep) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // nothings
                }
            }
        }

        // 表示获取失败
        throw new RuntimeException("异常");
    }

    /**
     * 获取偏移量
     *
     * @param consumer
     * @param groupID
     * @param topic
     * @param partitionID
     * @param clientName
     * @param whichTime
     * @return
     */
    private long getLastOffset(SimpleConsumer consumer, String groupID, String topic, int partitionID, String clientName, long whichTime) {
        // 1. 从保存offset偏移量的地方获取偏移量
        long offset = this.getOffsetOfTopicAndPartition(consumer, groupID, topic, partitionID, clientName);

        // 2. 如果获取的偏移量为0的情况下，重新从kafka集群获取偏移量
        if (offset != 0) return offset;

        // 构建topic相关参数
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        requestInfo.put(new TopicAndPartition(topic, partitionID), new PartitionOffsetRequestInfo(whichTime, 1));

        // 构建请求参数
        OffsetRequest request = new OffsetRequest(requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);

        // 请求数据
        OffsetResponse response = consumer.getOffsetsBefore(request);

        // 处理返回结果
        if (response.hasError()) {
            return -1;
        }

        long[] offsets = response.offsets(topic, partitionID);
        return offsets[0];
    }

    /**
     * 从保存offset偏移量的地方获取之前操作的偏移量
     *
     * @param consumer
     * @param groupID
     * @param topic
     * @param partitionID
     * @return
     */
    private long getOffsetOfTopicAndPartition(SimpleConsumer consumer, String groupID, String topic, int partitionID, String clientName) {
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partitionID);
        List<TopicAndPartition> requestInfo = new ArrayList<TopicAndPartition>();
        requestInfo.add(topicAndPartition);
        OffsetFetchRequest request = new OffsetFetchRequest(groupID, requestInfo, 0, clientName);
        OffsetFetchResponse response = consumer.fetchOffsets(request);

        Map<TopicAndPartition, OffsetMetadataAndError> returnOffsetMetadata = response.offsets();

        if (returnOffsetMetadata != null && !returnOffsetMetadata.isEmpty()) {
            OffsetMetadataAndError offset = returnOffsetMetadata.get(topicAndPartition);

            if (offset.error() == ErrorMapping.NoError()) {
                // 表示没有异常，返回数据接口
                return offset.offset();
            } else {
                // 表示异常
            }
        }
        return 0;
    }

    /**
     * 获取对应主题和分区的主Broker节点信息
     *
     * @param brokers     kafka集群连接参数，eg: hadoop-senior01,9092
     * @param topic       主题名称
     * @param partitionID 所需要读取数据的分区id的值
     * @return
     */
    private PartitionMetadata findLeader(List<KafkaBrokerInfo> brokers, String topic, int partitionID) {
        for (KafkaBrokerInfo info : brokers) {
            SimpleConsumer consumer = null;
            String host = info.host;
            int port = info.port;

            // 1. 构建连接对象
            consumer = new SimpleConsumer(host, port, 10000, 64 * 1024, "leaderLoopUp");

            // 2. 构建topic集合
            List<String> topics = Collections.singletonList(topic);

            // 3. 构建请求参数
            TopicMetadataRequest request = new TopicMetadataRequest(topics);

            // 4. 查询该topic对应的meta数据
            TopicMetadataResponse response = consumer.send(request);

            // 5. 获取topic的元数据
            List<TopicMetadata> metadatas = response.topicsMetadata();

            // 6. 遍历元数据，选择出主broker节点
            for (TopicMetadata metadata : metadatas) {
                // 获取metadata对应的topic
                String currentTopic = metadata.topic();
                // 如果topic是我们获取数据的topic，那么进行操作
                if (topic.equalsIgnoreCase(currentTopic)) {
                    // 遍历分区，选择当前分区
                    for (PartitionMetadata part : metadata.partitionsMetadata()) {
                        if (part.partitionId() == partitionID) {
                            // 找到了对应Topic的对应Parition的主Broker节点

                            // 获取当前分区的副本，方便后续进行leader切换的时候进行控制
                            List<Broker> replicasBrokers = part.replicas();
                            for (Broker broker : replicasBrokers) {
                                replicaBrokers.add(new KafkaBrokerInfo(broker.host(), broker.port()));
                            }

                            return part;
                        }
                    }
                }
            }
        }

        // 还是没有找到，直接返回null
        return null;
    }
}
