package com.lzhsite.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
/**
 * 通过"DatanodeInfo.getHostName()"可获取HDFS集群上的所有节点名称
 * @author lzhcode
 *
 */
public class GetList {
	 public static void main(String[] args) throws Exception {

	        Configuration conf=new Configuration();

	        FileSystem fs=FileSystem.get(conf);
	       
	        DistributedFileSystem hdfs = (DistributedFileSystem)fs;

	        DatanodeInfo[] dataNodeStats = hdfs.getDataNodeStats();

	       

	        for(int i=0;i<dataNodeStats.length;i++){

	            System.out.println("DataNode_"+i+"_Name:"+dataNodeStats[i].getHostName());

	        }

	    }
}
