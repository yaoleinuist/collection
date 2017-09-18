package com.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
/**
 * 通过"FileStatus.getPath()"可查看指定HDFS中某个目录下所有文件。
 * @author lzhcode
 *
 */
public class ListAllFile {

	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		FileSystem hdfs = FileSystem.get(conf);

		Path listf = new Path("/user/hadoop/test");

		FileStatus stats[] = hdfs.listStatus(listf);

		for (int i = 0; i < stats.length; ++i) {
			System.out.println(stats[i].getPath().toString());
		}

		hdfs.close();

	}

}