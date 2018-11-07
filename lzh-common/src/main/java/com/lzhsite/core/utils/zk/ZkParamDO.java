package com.lzhsite.core.utils.zk;

/**
 * @Description： zk初始化参数
 */
public class ZkParamDO {

    /**
     * session超时时间
     */
    private int zkSessionTimeout;
    /**
     * 连接超时时间
     */
    private int zkConnectionTimeout;
    /**
     * 根目录
     */
    private String root;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 服务地址
     */
    private String zkConnectString;
    /**
     * 是否验证父目录
     */
    private Boolean isCheckParentPath;


    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    public void setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    public int getZkConnectionTimeout() {
        return zkConnectionTimeout;
    }

    public void setZkConnectionTimeout(int zkConnectionTimeout) {
        this.zkConnectionTimeout = zkConnectionTimeout;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getZkConnectString() {
        return zkConnectString;
    }

    public void setZkConnectString(String zkConnectString) {
        this.zkConnectString = zkConnectString;
    }

    public Boolean getCheckParentPath() {
        return isCheckParentPath;
    }

    public void setCheckParentPath(Boolean checkParentPath) {
        isCheckParentPath = checkParentPath;
    }
}
