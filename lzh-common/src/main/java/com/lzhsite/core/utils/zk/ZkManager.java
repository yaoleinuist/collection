package com.lzhsite.core.utils.zk;

import java.util.List;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzhsite.core.constant.Constants;

import net.sf.ehcache.search.impl.BaseResult;

/**
 * @Description： zk管理器
 */
public class ZkManager {

    private static final Logger logger = LoggerFactory.getLogger(ZkManager.class);


    protected ZkClient zkClient = null;

    private ZkParamDO zkParamDO;

    public ZkManager(ZkParamDO zkParamDO){

        logger.info("zkManagerConnectString:"+zkParamDO.getZkConnectString());
        this.zkParamDO = zkParamDO;
        String zkServers = zkParamDO.getZkConnectString();
        int zkConnectionTimeout = zkParamDO.getZkConnectionTimeout();
        int sessionTimeout = zkParamDO.getZkSessionTimeout();
        String userName = zkParamDO.getUserName();
        String passwd = zkParamDO.getPassword();
        try {
            zkClient = new ZkClient(zkServers,sessionTimeout,zkConnectionTimeout);
            zkClient.setZkSerializer(new ZkFastjsonSerializer());
            zkClient.addAuthInfo("digest",(userName+":"+passwd).getBytes(Constants.CHARSET_NAME));
        } catch (Exception e) {
            logger.error("初始化连接ZK失败!",e);
        }
    }

    /**
     * 添加数据
     * @param dataPath
     * @param object
     * @return
     */
    public Boolean setData(String dataPath, Object object){
        try {
            if(!zkClient.exists(dataPath)){
                zkClient.createPersistent(dataPath);
            }
            zkClient.writeData(dataPath,object);
            return false;
        } catch (Exception e) {
            logger.error("持久化解释的资源管理注解配置失败！name:" + object.getClass().getName(),e);
            return true;
        }
    }

    /**
     * 获取数据
     *
     * @param dataPath
     * @return
     */
    public Object getData(String dataPath) {
        return zkClient.readData(dataPath,true);
    }


    public Object deleteNode(String dataPath) {
        return zkClient.delete(dataPath);
    }


    public List<String> getChildList(String dataPath){
        return zkClient.getChildren(dataPath);
    }

    public void createPersistent(String path) {
        zkClient.createPersistent(path);
    }

    public void createPersistentTrue(String path,boolean createParents) {
        zkClient.createPersistent(path,createParents);
    }

    public boolean exists(String path) {
        return zkClient.exists(path);
    }

    public List<String> subscribeChildChanges(String dataPath){
        return zkClient.subscribeChildChanges(dataPath, new IZkChildListener(){

            /**
             * Called when the children of the given path changed.
             *
             * @param parentPath    The parent path
             * @param currentChilds The children or null if the root node (parent path) was deleted.
             * @throws Exception
             */
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                logger.info("目录"+parentPath+"子节点发生变化!现存节点如下：");
                if(null != currentChilds) {
                    for (int i = 0; i < currentChilds.size(); i++) {
                        logger.info("handleChildChange currentChilds:" + currentChilds.get(i));
                    }
                }

            }
        });
    }

    public void subscribeDataChanges(List<String> pathList){
        pathList.stream().forEach(path->{
            zkClient.subscribeDataChanges(path.toString(), new IZkDataListener() {
                @Override
                public void handleDataChange(String dataPath, Object data) throws Exception {
                    logger.info("目录" + dataPath + "数据发生变化!");

                }

                @Override
                public void handleDataDeleted(String dataPath) throws Exception {
                    logger.info("目录" + dataPath + "数据已被删除!");
                }
            });
        });
    }

}
