package com.lzhsite.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class CopyFile {
	public static void main(String[] args) throws Exception {

		Configuration conf = new Configuration();

		FileSystem hdfs = FileSystem.get(conf);

		// 本地文件

		Path src = new Path("D://大数据//hadoop//大数据Hadoop入门基础必知必会//03.随堂笔记//1.15.txt");

		// HDFS为止

		Path dst = new Path("/opt/datas/wc.input");

		hdfs.copyFromLocalFile(src, dst);

		System.out.println("Upload to" + conf.get("fs.default.name"));

		FileStatus files[] = hdfs.listStatus(dst);

		for (FileStatus file : files) {

			System.out.println(file.getPath());

		}

	}
}
