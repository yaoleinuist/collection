package com.lzhsite.zookeeper.configmag.demo1;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
/**
 * 配置管理
 * 配置中心一般用作系统的参数配置，它需要满足如下几个要求：高效获取、实时感知、分布式访问。
 * 对于一些少量频次访问的场景我们可以使用mysql数据库实现，但是有些参数在系统中访问频次较高，
 * 甚至是接口每访问一次就需要调起获取一次特别在是大规模系统访问量的情况下，
 * 我们就需要一个高效获取实时感知的分布式配置中心。本章节我们使用zookeeper来实现一个分布式配置管理中心组件。
 * @author lzhcode
 *
 */
public class MyClient implements Watcher {

	/**
	 * @param args
	 */
	public static String url = "192.168.226.3:2181";

	private final static String root = "/myConf" ;
	//   数据库连接  URL ,username  ,passwd
	private  String UrlNode = root  + "/url";
	private  String userNameNode = root  + "/username";
	private  String passWdNode = root  + "/passwd";
	
	
	public static String authType = "digest" ;
	public static String authPasswd = "password" ;
	
	private String uRLString ;
	private String username ;
	private String passwd ;
	
	ZooKeeper zk = null;
	
	public String getuRLString() {
		return uRLString;
	}
	public void setuRLString(String uRLString) {
		this.uRLString = uRLString;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	public void initValue()
	{
		try {
			//url未设置监听，服务端数据改变时无法实时获取
			uRLString = new String(zk.getData(UrlNode, false, null)) ;
			username = new String(zk.getData(userNameNode, true, null)) ;
			passwd = new String(zk.getData(passWdNode, true, null)) ;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public ZooKeeper getZK() throws Exception
	{
		zk = new ZooKeeper(url, 3000, this);
		zk.addAuthInfo(authType, authPasswd.getBytes());
		
		while (zk.getState() != ZooKeeper.States.CONNECTED) {
			Thread.sleep(3000);
		}
		return zk;
	}
	
	
	public static void main(String[] args) throws Exception {
		MyClient zkTest2 = new MyClient();
		ZooKeeper zk = zkTest2.getZK() ;
		zkTest2.initValue() ;
		int i=0;
		while(true){
			System.out.println(zkTest2.getuRLString());
			System.out.println(zkTest2.getUsername());
			System.out.println(zkTest2.getPasswd());
			System.out.println("-------------------------------------------");
			Thread.sleep(10000);
			i++;
			if(i==10)
			{
				break;
			}
		}

		zk.close();

	}

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		if (event.getType() == Watcher.Event.EventType.None) {
			System.out.println("连接服务器成功！");
		} else if (event.getType() == Watcher.Event.EventType.NodeCreated) {
			System.out.println("节点创建成功！");
		} else if (event.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
			System.out.println("子节点创建更新成功！");
			//读取新的配置
			initValue() ;
			
		} else if (event.getType() == Watcher.Event.EventType.NodeDataChanged) {
			System.out.println("节点更新成功！");
			//读取新的配置
			initValue() ;
			
		} else if (event.getType() == Watcher.Event.EventType.NodeDeleted) {
			System.out.println("节点删除成功！");
		}

	}

}
