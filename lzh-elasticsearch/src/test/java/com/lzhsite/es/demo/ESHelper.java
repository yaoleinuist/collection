package com.lzhsite.es.demo;

import java.net.InetAddress;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ESHelper {

	private static Client client = null;
	// 打开连接
	public static Client getESClient(){
		try{
			client = TransportClient.builder().build()
			        .addTransportAddress(
			        	new InetSocketTransportAddress(
			        		InetAddress.getByName("192.168.226.3"), 9300)
			            );
			return client;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void closeESClient(){
		if(client!=null){
			client.close();
		}
	}
	
}
