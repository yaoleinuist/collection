package com.zookeeper.lock;
//2
import java.net.InetAddress;
import java.net.UnknownHostException;
 
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
/*
	zookeeper集群的每个节点的数据都是一致的, 那么我们可以通过这些节点来作为锁的标志.
	利用了每个节点的强一致性
	首先操作要包含, lock(锁住), unlock(解锁), isLocked(是否锁住)三个方法
	然后我们可以创建一个工厂(LockFactory), 用来专门生产锁.
	锁的创建过程如下描述:
	前提:每个锁都需要一个路径来指定(如:/lock)
	1.根据指定的路径, 查找zookeeper集群下的这个节点是否存在.(说明已经有锁了)
	2. 如果存在, 根据查询者的一些特征数据(如ip地址/hostname), 当前的锁是不是查询者的
	3. 如果不是查询者的锁, 则返回null, 说明创建锁失败
	4. 如果是查询者的锁, 则把这个锁返回给查询者
	5. 如果这个节点不存在, 说明当前没有锁, 那么创建一个临时节点, 并将查询者的特征信息写入这个节点的数据中, 然后返回这个锁.
	根据以上5部, 一个分布式的锁就可以创建了.
	创建的锁有三种状态:
	1. 创建失败(null), 说明该锁被其他查询者使用了
	2. 创建成功, 但当前没有锁住(unlocked), 可以使用
	3. 创建成功, 但当前已经锁住(locked)了, 不能继续加锁.
*/
public class Lock {
    private String path;
    private ZooKeeper zooKeeper;
    public Lock(String path){
        this.path = path;
    }
     
    /**
     * 方法描述: 上锁 lock it
     */
    public synchronized void lock() throws Exception{
        Stat stat = zooKeeper.exists(path, true);
        String data = InetAddress.getLocalHost().getHostAddress()+":lock";
        zooKeeper.setData(path, data.getBytes(), stat.getVersion());
    }
     
    /**
     * 方法描述：开锁 unlock it
     */
    public synchronized void unLock() throws Exception{
        Stat stat = zooKeeper.exists(path, true);
        String data = InetAddress.getLocalHost().getHostAddress()+":unlock";
        zooKeeper.setData(path, data.getBytes(), stat.getVersion());
    }
     
    /**
     * 方法描述：是否锁住了, isLocked?
     */
    public synchronized boolean isLock(){
        try {
            Stat stat = zooKeeper.exists(path, true);
            String data = InetAddress.getLocalHost().getHostAddress()+":lock";
            String nodeData = new String(zooKeeper.getData(path, true, stat));
            if(data.equals(nodeData)){
                return true;
            }
        } catch (UnknownHostException e) {
        } catch (KeeperException e) {
        } catch (InterruptedException e) {
        }
        return false;
    }
 
    public String getPath() {
        return path;
    }
 
    public void setPath(String path) {
        this.path = path;
    }
 
    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }
     
     
}