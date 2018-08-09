package com.lzhsite.zookeeper.rmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RMIClient {

    private static final Logger logger = LoggerFactory
            .getLogger(RMIClient.class);
	 
 
    // 用于等待 SyncConnected 事件触发后继续执行当前线程
    private CountDownLatch latch = new CountDownLatch(1);
 
    // 定义一个 volatile 成员变量，用于保存最新的 RMI 地址（考虑到该变量或许会被其它线程所修改，一旦修改后，该变量的值会影响到所有线程）
    private volatile HashMap<String,List<String>> dataMap = new HashMap<String, List<String>>();
 
    private Lock _lock = new ReentrantLock();
    
    private static  ZooKeeper zk;
    
    // 构造器
    public RMIClient() {
        zk = connectServer(); // 连接 ZooKeeper 服务器并获取 ZooKeeper 对象
        watchNode();
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.currentThread().sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    _lock.lock();
                    if (zk != null) {
                        if (zk.getState().isAlive()
                                && zk.getState().isConnected()) {
                            _lock.unlock();
                            continue;
                        }
                    }
                    if(zk!=null){
                        try {
                            zk.close();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        zk = null;
                    }
                    zk = connectServer();
                    _lock.unlock();
                }
            }
        }).start();
    }
 
    // 查找 RMI 服务
    public <T extends Remote> T lookup(String key) {
        T service = null;
        int size = dataMap.size();
        if (size > 0) {
            String url = null;
            if(dataMap.containsKey(key)){
                List<String> urlList = dataMap.get(key);
                if(urlList.size()>0){
                    if(urlList.size()==1){
                         url = urlList.get(0);
                    }else{
                        url = urlList.get(ThreadLocalRandom.current().nextInt(size)); 
                    }
                }
                 service = lookupService(url,key); // 从 JNDI 中查找 RMI 服务
            }
        }
        return service;
    }
 
    // 连接 ZooKeeper 服务器
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(ConfigHelp.ZK_CONNECTION_STRING, ConfigHelp.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown(); // 唤醒当前正在执行的线程
                    }
                }
            });
            latch.await(); // 使当前线程处于等待状态
        } catch (IOException | InterruptedException e) {
            logger.error("", e);
        }
        return zk;
    }
 
    // 观察 /registry 节点下所有子节点是否有变化
    private void watchNode() {
        _lock.lock();
        if(zk!=null&&zk.getState().isAlive()&&zk.getState().isConnected()){
            
        }else{
            if(zk!=null){
                try {
                    zk.close();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                zk = null;
            }
            zk = connectServer();
        }
        try {
            List<String> nodeList = zk.getChildren(ConfigHelp.ZK_RMI_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getType() == Event.EventType.NodeChildrenChanged) {
                        watchNode(); // 若子节点有变化，则重新调用该方法（为了获取最新子节点中的数据）
                    }
                }
            });
            List<String> dataList = new ArrayList<>(); // 用于存放 /registry 所有子节点中的数据
            HashMap<String,List<String>> dataMap = new HashMap<String, List<String>>();
            for (String node : nodeList) {
                byte[] data = zk.getData(ConfigHelp.ZK_RMI_PATH + "/" + node, false, null); // 获取 /registry 的子节点中的数据
                dataList.add(new String(data));
                String d = new String(data).toString();
                String key = d.split("#:#")[0];
                String url = d.split("#:#")[1];
                if(dataMap.containsKey(key)){
                    dataMap.get(key).add(url);
                }else{
                    List<String> list = new ArrayList<String>();
                    list.add(url);
                    dataMap.put(key, list);
                }
            }
            logger.debug("node data: {}", dataList);
            this.dataMap = dataMap;
        } catch (KeeperException | InterruptedException e) {
            logger.error("", e);
        }
        _lock.unlock();
    }
 
    // 在 JNDI 中查找 RMI 远程服务对象
    @SuppressWarnings("unchecked")
    private <T> T lookupService(String url,String key) {
        T remote = null;
        try {
            remote = (T) Naming.lookup(url);
        } catch (NotBoundException | MalformedURLException | RemoteException e) {
            if (e instanceof ConnectException) {
                // 若连接中断，则使用 urlList 中第一个 RMI 地址来查找（这是一种简单的重试方式，确保不会抛出异常）
                logger.error("ConnectException -> url: {}", url);
                if(dataMap.containsKey(key)){
                    List<String> urlList = dataMap.get(key);
                    if(urlList.size()>0){
                        return lookupService(urlList.get(0),key);
                    }
                }
            }
            logger.error("", e);
        }
        return remote;
    }
    public static void main(String[] args) {
        RMIClient client = new RMIClient();
        while(true){
            Service service = client.lookup("Test");
            service.service("test12");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}