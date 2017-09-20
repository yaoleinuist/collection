package com.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class Rename {
	public static void main(String[] args) throws Exception {

        Configuration conf=new Configuration();

        FileSystem hdfs=FileSystem.get(conf);


        Path frpaht=new Path("/user/beifeng/wordcount/output6/WordCountMapReduce.txt");    //旧的文件名
        Path topath=new Path("/user/beifeng/wordcount/output6/WordCountReduce.txt");    //新的文件名

        boolean isRename=hdfs.rename(frpaht, topath);

       
        String result=isRename?"成功":"失败";

        System.out.println("文件重命名结果为："+result);

       

    }
}
