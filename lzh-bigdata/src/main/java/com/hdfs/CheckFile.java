package com.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
/**
 * 　通过"FileSystem.exists（Path f）"可查看指定HDFS文件是否存在，其中f为文件的完整路径
 * @author lzhcode
 *
 */
public class CheckFile {
	 public static void main(String[] args) throws Exception {

	        Configuration conf=new Configuration();

	        FileSystem hdfs=FileSystem.get(conf);

	        Path findf=new Path("/test1");

	        boolean isExists=hdfs.exists(findf);

	        System.out.println("Exist?"+isExists);

	    }
}
