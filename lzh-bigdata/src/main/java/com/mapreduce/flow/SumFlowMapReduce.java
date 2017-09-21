package com.mapreduce.flow;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by Administrator on 2017/3/22.
 */

/**
 * 手机流量上下行统计
 */
public class SumFlowMapReduce {
	/**
	 * input -> map ->reduce ->output input 一行一行读取的 <每一行的偏移量，每一行的内容> -> <0,
	 * Mapper<LongWritable,Text, 输入文件每行偏移量，每行内容
	 * Text,IntWritable> map输出的单词，单词出现的次数
	 */
	public static class SumFlowMapper extends
			Mapper<LongWritable, Text, Text, FlowWritable> {
		/**
		 * 读到日志的一行数据，切分成一个一个的字段，取出需要的字段
		 * key：手机号  value：取出上行、下行 封装到FlowWritable对象
		 */
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// line 每一行的内容 <key：0, value:{hadoop java}> Text -> String
			String line = value.toString();

			// split 切分每一行的内容 String ->String[]
			String[] fields = line.split("\t");

			//取出需要的字段
			String phoneNm = fields[1];
			long up_flow = Long.parseLong(fields[8]);
			long down_flow = Long.parseLong(fields[9]);
			long sum_flow = up_flow+down_flow;
			
			//将取出的字段放入mapout的key，value中
			context.write(new Text(phoneNm), new FlowWritable(up_flow, down_flow, sum_flow));

		}
	}

	/**
	 * shuffle group :将相同的key放到一个组,将对应的每个value放到一个集合 <key,list[1,1,1,1...]
	 * 
	 */
	public static class SumFlowReducer extends
			Reducer<Text, FlowWritable, Text, FlowWritable> {
		// phoneNM list[FlowWritable1,FlowWritable2 。。]
		@Override
		protected void reduce(Text key, Iterable<FlowWritable> values,
				Context context) throws IOException, InterruptedException {
			//定义各统计流量之和的变量及初始值
			long up_flow_sum = 0;    //上行流量之和
			long down_flow_sum = 0;  //下行流量之和
			long sum_flow = 0;       //上行和下行流量总和
			
			//遍历相同手机号对应的 FlowBean集合 
			for(FlowWritable b : values){
				up_flow_sum += b.getUp_flow();
				down_flow_sum += b.getDown_flow();
			}
			sum_flow = up_flow_sum+down_flow_sum;
			
			context.write(key,new FlowWritable(up_flow_sum,down_flow_sum,sum_flow));
			
		}
	}

	public int run(String[] args) throws IOException, ClassNotFoundException,
			InterruptedException {
		// 1.获取hadoop的配置信息
		Configuration config = new Configuration();
	

		// 2.生成对应的job
		Job job = Job.getInstance(config, this.getClass().getSimpleName());
		job.setJarByClass(this.getClass());

		// 3:设置job内容
		// input -> map ->reduce -> output
		// 3.1 输入
		Path inPath = new Path(args[0]);
		FileInputFormat.setInputPaths(job, inPath);

		// 3.2:map
		job.setMapperClass(SumFlowMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(FlowWritable.class);

		// 3.reducer
		job.setReducerClass(SumFlowReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(FlowWritable.class);
//		job.setNumReduceTasks(3);

		// 3.4输出
		Path outPath = new Path(args[1]);
		// 输出目录如果存在自动删除
		FileSystem fsl = FileSystem.get(config);
		FileSystem fsh = outPath.getFileSystem(config);
		if (fsh.exists(outPath)) {
			fsh.delete(outPath, true);
		}
		FileOutputFormat.setOutputPath(job, outPath);

		// 4.提交的job运行是否成功
		boolean isSuccess = job.waitForCompletion(true);
		return isSuccess ? 0 : 1;

	}

	public static void main(String[] args) throws InterruptedException,
			IOException, ClassNotFoundException {
		args = new String[] {
				"hdfs://ns1/user/beifeng/wordcount/input/HTTP_20130313143750.data",
				"hdfs://ns1/user/beifeng/wordcount/output10" };

		
		int status = new SumFlowMapReduce().run(args);

		System.exit(status);
	}
}
