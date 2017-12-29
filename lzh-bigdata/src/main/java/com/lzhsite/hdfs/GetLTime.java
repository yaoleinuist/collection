package com.lzhsite.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
/**
 * 通过"FileSystem.getModificationTime()"可查看指定HDFS文件的修改时间。具体实现如下：
 * @author lzhcode
 *
 */
public class GetLTime {
	public static void main(String[] args) throws Exception {

        Configuration conf=new Configuration();

        FileSystem hdfs=FileSystem.get(conf);

       

        Path fpath =new Path("/user/beifeng/wc.input/1.15");


        FileStatus fileStatus=hdfs.getFileStatus(fpath);

        long modiTime=fileStatus.getModificationTime();

       

        System.out.println("1.15的修改时间是"+modiTime);

    }
}
