package com.lzhsite.zookeeper.rmi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzhsite.zookeeper.rmi.impl.ServiceImpl;


public class RMIServer {

    private static final Logger logger = LoggerFactory
            .getLogger(RMIServer.class);

    private static ZooKeeper zk;

    private boolean isRegistry = false;

    //同步锁
    private Lock _lock = new ReentrantLock();
    
    private static final Map<String,String> CACHED_URL = new HashMap<String,String>();

    public RMIServer() {
        zk = connectServer();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.currentThread().sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    logger.info("check zk...");
                    _lock.lock();
                    if (zk != null) {
                        if (zk.getState().isAlive()
                                && zk.getState().isConnected()) {
                            logger.info("zk is ok");
                            _lock.unlock();
                            continue;
                        }
                    }
                    close();
                    logger.info("reConnectServer ...");
                    zk = connectServer();
                    Iterator<String> it = CACHED_URL.keySet().iterator();
                    while(it.hasNext()){
                        String key = it.next();
                        String url = CACHED_URL.get(key);
                        createNode(zk,url,key);
                    }
                    logger.info("reConnectServer ok");
                    _lock.unlock();
                }

            }

            private void close() {
                if(zk!=null){
                    try {
                        zk.close();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    zk = null;
                }
            }
        }).start();
    }

    // 用于等待 SyncConnected 事件触发后继续执行当前线程
    private CountDownLatch latch = new CountDownLatch(1);

    // 发布 RMI 服务并注册 RMI 地址到 ZooKeeper 中
    public void publish(Remote remote, String key) {
        _lock.lock();
        String url = publishService(remote); // 发布 RMI 服务并返回 RMI 地址
        if (url != null) {
            if (zk != null) {
                if (zk.getState().isAlive() && zk.getState().isConnected()) {
                } else {
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
                createNode(zk, url, key); // 创建 ZNode 并将 RMI 地址放入 ZNode 上
            }
        }
        _lock.unlock();

    }

    // 发布 RMI 服务
    private String publishService(Remote remote) {
        String url = null;
        try {
            String host = ConfigHelp.getLocalConifg("rmiIP", "127.0.0.1");
            int port = Integer.valueOf(ConfigHelp.getLocalConifg("rmiPort",
                    "10990"));
            url = String.format("rmi://%s:%d/%s", host, port, remote.getClass()
                    .getName());
            if (!isRegistry) {
                LocateRegistry.createRegistry(port);
                isRegistry = true;
            }
            Naming.rebind(url, remote);
            logger.debug("publish rmi service (url: {})", url);
        } catch (RemoteException | MalformedURLException e) {
            logger.error("", e);
        }
        return url;
    }

    // 连接 ZooKeeper 服务器
    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {

            zk = new ZooKeeper(ConfigHelp.ZK_CONNECTION_STRING,
                    ConfigHelp.ZK_SESSION_TIMEOUT, new Watcher() {
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
        if (zk != null) {
            try {
                Stat stat = zk.exists(ConfigHelp.ZK_ROOT_PATH, false);
                if (stat == null) {
                    String path = zk.create(ConfigHelp.ZK_ROOT_PATH,
                            "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT); // 创建一个临时性且有序的 ZNode
                    logger.info("create zookeeper node ({})", path);
                }
                stat = zk.exists(ConfigHelp.ZK_RMI_PATH, false);
                if (stat == null) {

                    String path = zk.create(ConfigHelp.ZK_RMI_PATH,
                            "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                            CreateMode.PERSISTENT); // 创建一个临时性且有序的 ZNode
                    logger.info("create zookeeper node ({})", path);
                }
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return zk;
    }

    // 创建 ZNode
    private void createNode(ZooKeeper zk, String url, String key) {
        try {
            CACHED_URL.put(key, url);
            byte[] data = (key + "#:#" + url).getBytes();
            String path = zk.create(ConfigHelp.ZK_RMI_PATH + "/", data,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL); // 创建一个临时性且有序的 ZNode
            logger.info("create zookeeper node ({} => {})", path, url);
        } catch (KeeperException | InterruptedException e) {
            logger.error("", e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        RMIServer server = new RMIServer();
        Service service = new ServiceImpl();
        server.publish(service, "Test");
        server.publish(service, "Test1");
        Thread.currentThread().sleep(50000);
        server.publish(service, "Test3");
        Thread.currentThread().sleep(Integer.MAX_VALUE);
    }
}