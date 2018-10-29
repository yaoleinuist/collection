package com.lzhsite.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class TestHBaseDriver extends Configured implements Tool{

	@Override
	public int run(String[] args) throws Exception {
		
		Configuration conf = this.getConf();
		//job
		Job job = Job.getInstance(conf, "mr-hbase");
		job.setJarByClass(TestHBaseDriver.class);
		
		Scan scan = new Scan();
		TableMapReduceUtil.initTableMapperJob(
				  "stu_info",        // input table
				  scan,               // Scan instance to control CF and attribute selection
				  TestHBaseMapper.class,     // mapper class
				  ImmutableBytesWritable.class,         // mapper output key
				  Put.class,  // mapper output value
				  job,false);  //最后一个参数表示是否打包运行
				//job);   
		TableMapReduceUtil.initTableReducerJob(
				  "t5",        // output table
				  null,    // reducer class
				  job,null,null,null,null,false); //最后一个参数表示是否打包运行
				  // job);
				job.setNumReduceTasks(1);   // at least one, adjust as required
		return job.waitForCompletion(true) ? 0:1;
	}
	
	public static void main(String[] args) {
		
		Configuration conf = HBaseConfiguration.create();
		try {
			int status = ToolRunner.run(conf, new TestHBaseDriver(), args);
			System.exit(status);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
