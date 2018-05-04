package com.lzhsite.spring.web.config;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.AnnotationBean;

import lombok.Data;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

 
@Configuration
@RefreshScope
@Data
@Component("DubboConfig")
public class DubboConfig {
    @Value("${spring.dubbo.application.name}")
    private String applicationName;

    @Value("${spring.dubbo.application.environment}")
    private String applicationEnvironment;

    @Value("${spring.dubbo.registry.protocol}")
    private String protocol;

    @Value("${spring.dubbo.registry.address}")
    private String registryAddress;

    @Value("${spring.dubbo.monitor.protocol}")
    private String monitorProtocol;
    @Value("${spring.dubbo.transaction.filter}")
    private String  transactionFilter;

    private static ZooKeeper zooKeeper = null;

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public synchronized boolean getZooKeeperState(){
        boolean zkFlag = false;
        if(zooKeeper==null||ZooKeeper.States.CLOSED.equals(zooKeeper.getState())){
            try {
                zooKeeper = new ZooKeeper(registryAddress,50000000,new Watcher(){
                    @Override
                    public void process(WatchedEvent event) {
                        if(Watcher.Event.KeeperState.SyncConnected == event.getState()){
                            countDownLatch.countDown();
                        }
                    }
                });
            }catch (Exception e){

            }
        }
        if(zooKeeper!=null&&ZooKeeper.States.CONNECTED.equals(zooKeeper.getState())){
            zkFlag = true;
        }
        return zkFlag;
    }

    /**
     * 设置dubbo扫描包
     *
     * @param
     * @return
     */
    @Bean
    public static AnnotationBean annotationBean(@Value("${spring.dubbo.scan}") String packageName) {
        AnnotationBean annotationBean = new AnnotationBean();
        annotationBean.setPackage(packageName);
        return annotationBean;
    }
    
    /**
     * 注入dubbo注册中心配置,基于zookeeper
     *
     * @return
     */
    @Bean
    public RegistryConfig registryConfig() {
        // 连接注册中心配置
        RegistryConfig registry = new RegistryConfig();
        registry.setProtocol(protocol);
        registry.setAddress(registryAddress);
        return registry;
    }
    
    /**
     * dubbo消费
     *
     * @param applicationConfig
     * @param registryConfig
     * @return
     */
    @Bean(name="defaultConsumer")
    public ConsumerConfig providerConfig(ApplicationConfig applicationConfig, RegistryConfig registryConfig) {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setApplication(applicationConfig);
        consumerConfig.setRegistry(registryConfig);
        consumerConfig.setFilter(transactionFilter);
        return consumerConfig;
    }
    
}
 