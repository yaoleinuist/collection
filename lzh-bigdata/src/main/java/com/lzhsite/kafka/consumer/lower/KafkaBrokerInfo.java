package com.lzhsite.kafka.consumer.lower;

/**
 * Created by ibf on 01/11.
 */
public class KafkaBrokerInfo {
    public String host;
    public int port;

    public KafkaBrokerInfo() {
        this("", 0);
    }

    public KafkaBrokerInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }
}
