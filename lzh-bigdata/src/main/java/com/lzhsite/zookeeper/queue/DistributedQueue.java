package com.lzhsite.zookeeper.queue;
 

import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

/**
 * 
 *	分布式队列是通用的数据结构，为了在 Zookeeper 中实现分布式队列，首先需要指定一个 Znode 
 *  节点作为队列节点（queue node）， 各个分布式客户端通过调用 create() 函数向队列中放入数据，
 *  调用create()时节点路径名带"qn-"结尾，并设置顺序（sequence）节点标志。 
 *  由于设置了节点的顺序标志，新的路径名具有以下字符串模式："_path-to-queue-node_/qn-X",X 是唯一自增号。
 *  需要从队列中获取数据/移除数据的客户端首先调用 getChildren() 函数，有数据则获取（获取数据后可以删除也可以不删），
 *  没有则在队列节点（queue node）上将 watch 设置为 true，等待触发并处理最小序号的节点（即从序号最小的节点中取数据）。
 *	实现步骤基本如下：
 *	前提：需要一个队列root节点dir
 *	入队：使用create()创建节点，将共享数据data放在该节点上，节点类型为PERSISTENT_SEQUENTIAL，永久顺序性的
 * （也可以设置为临时的，看需求）。
 *  出队：因为队列可能为空，2种方式处理：一种如果为空则wait等待，一种返回异常。
 *	等待方式：这里使用了CountDownLatch的等待和Watcher的通知机制，使用了TreeMap的排序获取节点顺序最小的数据（FIFO）。
 *	抛出异常：getChildren()获取队列数据时，如果size==0则抛出异常。
 * 
 */
public class DistributedQueue {
    private static final Logger LOG = LoggerFactory.getLogger(DistributedQueue.class);

    private final String dir;

    private ZooKeeper zookeeper;
    private List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;

    private final String prefix = "qn-";

    public DistributedQueue(ZooKeeper zookeeper, String dir, List<ACL> acl){
        this.dir = dir;

        if(acl != null){
            this.acl = acl;
        }
        this.zookeeper = zookeeper;
        
        //Add root dir first if not exists
        if (zookeeper != null) {
            try {
                Stat s = zookeeper.exists(dir, false);
                if (s == null) {
                    zookeeper.create(dir, new byte[0], acl, CreateMode.PERSISTENT);
                }
            } catch (KeeperException e) {
                LOG.error(e.toString());
            } catch (InterruptedException e) {
                LOG.error(e.toString());
            }
        }
    }

    /**
     * Returns a Map of the children, ordered by id.
     * @param watcher optional watcher on getChildren() operation.
     * @return map from id to child name for all children
     */
    private TreeMap<Long,String> orderedChildren(Watcher watcher) throws KeeperException, InterruptedException {
        TreeMap<Long,String> orderedChildren = new TreeMap<Long,String>();

        List<String> childNames = null;
        try{
            childNames = zookeeper.getChildren(dir, watcher);
        }catch (KeeperException.NoNodeException e){
            throw e;
        }

        for(String childName : childNames){
            try{
                //Check format
                if(!childName.regionMatches(0, prefix, 0, prefix.length())){
                    LOG.warn("Found child node with improper name: " + childName);
                    continue;
                }
                String suffix = childName.substring(prefix.length());
                Long childId = new Long(suffix);
                orderedChildren.put(childId,childName);
            }catch(NumberFormatException e){
                LOG.warn("Found child node with improper format : " + childName + " " + e,e);
            }
        }

        return orderedChildren;
    }

    /**
     * Find the smallest child node.
     * @return The name of the smallest child node.
     */
    private String smallestChildName() throws KeeperException, InterruptedException {
        long minId = Long.MAX_VALUE;
        String minName = "";

        List<String> childNames = null;

        try{
            childNames = zookeeper.getChildren(dir, false);
        }catch(KeeperException.NoNodeException e){
            LOG.warn("Caught: " +e,e);
            return null;
        }

        for(String childName : childNames){
            try{
                //Check format
                if(!childName.regionMatches(0, prefix, 0, prefix.length())){
                    LOG.warn("Found child node with improper name: " + childName);
                    continue;
                }
                String suffix = childName.substring(prefix.length());
                long childId = Long.parseLong(suffix);
                if(childId < minId){
                    minId = childId;
                    minName = childName;
                }
            }catch(NumberFormatException e){
                LOG.warn("Found child node with improper format : " + childName + " " + e,e);
            }
        }

        if(minId < Long.MAX_VALUE){
            return minName;
        }else{
            return null;
        }
    }

    /**
     * Return the head of the queue without modifying the queue.
     * @return the data at the head of the queue.
     * @throws NoSuchElementException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] element() throws NoSuchElementException, KeeperException, InterruptedException {
        TreeMap<Long,String> orderedChildren;

        // element, take, and remove follow the same pattern.
        // We want to return the child node with the smallest sequence number.
        // Since other clients are remove()ing and take()ing nodes concurrently, 
        // the child with the smallest sequence number in orderedChildren might be gone by the time we check.
        // We don't call getChildren again until we have tried the rest of the nodes in sequence order.
        while(true){
            try{
                orderedChildren = orderedChildren(null);
            }catch(KeeperException.NoNodeException e){
                throw new NoSuchElementException();
            }
            if(orderedChildren.size() == 0 ) throw new NoSuchElementException();

            for(String headNode : orderedChildren.values()){
                if(headNode != null){
                    try{
                        return zookeeper.getData(dir+"/"+headNode, false, null);
                    }catch(KeeperException.NoNodeException e){
                        //Another client removed the node first, try next
                    }
                }
            }

        }
    }


    /**
     * Attempts to remove the head of the queue and return it.
     * @return The former head of the queue
     * @throws NoSuchElementException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] remove() throws NoSuchElementException, KeeperException, InterruptedException {
        TreeMap<Long,String> orderedChildren;
        // Same as for element.  Should refactor this.
        while(true){
            try{
                orderedChildren = orderedChildren(null);
            }catch(KeeperException.NoNodeException e){
                throw new NoSuchElementException();
            }
            if(orderedChildren.size() == 0) throw new NoSuchElementException();

            for(String headNode : orderedChildren.values()){
                String path = dir +"/"+headNode;
                try{
                    byte[] data = zookeeper.getData(path, false, null);
                    zookeeper.delete(path, -1);
                    return data;
                }catch(KeeperException.NoNodeException e){
                    // Another client deleted the node first.
                }
            }
        }
    }

    private class LatchChildWatcher implements Watcher {

        CountDownLatch latch;

        public LatchChildWatcher(){
            latch = new CountDownLatch(1);
        }

        public void process(WatchedEvent event){
            LOG.debug("Watcher fired on path: " + event.getPath() + " state: " + 
                    event.getState() + " type " + event.getType());
            latch.countDown();
        }
        public void await() throws InterruptedException {
            latch.await();
        }
    }

    /**
     * Removes the head of the queue and returns it, blocks until it succeeds.
     * @return The former head of the queue
     * @throws NoSuchElementException
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] take() throws KeeperException, InterruptedException {
        TreeMap<Long,String> orderedChildren;
        // Same as for element.  Should refactor this.
        while(true){
            LatchChildWatcher childWatcher = new LatchChildWatcher();
            try{
                orderedChildren = orderedChildren(childWatcher);
            }catch(KeeperException.NoNodeException e){
                zookeeper.create(dir, new byte[0], acl, CreateMode.PERSISTENT);
                continue;
            }
            if(orderedChildren.size() == 0){
                childWatcher.await();
                continue;
            }

            for(String headNode : orderedChildren.values()){
                String path = dir +"/"+headNode;
                try{
                    byte[] data = zookeeper.getData(path, false, null);
                    zookeeper.delete(path, -1);
                    return data;
                }catch(KeeperException.NoNodeException e){
                    // Another client deleted the node first.
                }
            }
        }
    }

    /**
     * Inserts data into queue.
     * @param data
     * @return true if data was successfully added
     */
    public boolean offer(byte[] data) throws KeeperException, InterruptedException{
        for(;;){
            try{
                zookeeper.create(dir+"/"+prefix, data, acl, CreateMode.PERSISTENT_SEQUENTIAL);
                return true;
            }catch(KeeperException.NoNodeException e){
                zookeeper.create(dir, new byte[0], acl, CreateMode.PERSISTENT);
            }
        }
    }

    /**
     * Returns the data at the first element of the queue, or null if the queue is empty.
     * @return data at the first element of the queue, or null.
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] peek() throws KeeperException, InterruptedException{
        try{
            return element();
        }catch(NoSuchElementException e){
            return null;
        }
    }

    /**
     * Attempts to remove the head of the queue and return it. Returns null if the queue is empty.
     * @return Head of the queue or null.
     * @throws KeeperException
     * @throws InterruptedException
     */
    public byte[] poll() throws KeeperException, InterruptedException {
        try{
            return remove();
        }catch(NoSuchElementException e){
            return null;
        }
    }
}