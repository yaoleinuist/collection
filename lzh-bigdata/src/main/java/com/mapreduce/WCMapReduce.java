package com.mapreduce;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * job.setCombinerClass(WCCombiner.class);
 * 可选项减少网络传输，减少本地磁盘IO流的读写
 * 把reduce操作放在map阶段操作，是个可选项
 * @author lzhcode
 *
 */

public class WCMapReduce extends Configured implements Tool {

	// step 1: Mapper
	public static class WCMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		private Text mapOutputKey = new Text();
		private IntWritable mapOutputValue = new IntWritable(1);

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			// line value
			String lineValue = value.toString();

			// spilt
			String[] strs = lineValue.split(" ");

			// iterator
			for (String str : strs) {

				mapOutputKey.set(str);

				context.write(mapOutputKey, mapOutputValue);

				System.out.println("<" + mapOutputKey + "," + mapOutputValue
						+ ">");

			}
		}

	}

	// step 2: Reducer
	public static class WCCombiner extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable outputValue = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			System.out.println("key = " + key);
			// temp sum
			int sum = 0;

			// iterator
			for (IntWritable value : values) {
				sum += value.get();

				System.out.print(value.get() + " ");
			}

			System.out.println();

			// set output
			outputValue.set(sum);

			context.write(key, outputValue);

		}

	}

	// step 2: Reducer
	public static class WCReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable outputValue = new IntWritable();

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			// temp sum
			int sum = 0;

			// iterator
			for (IntWritable value : values) {
				sum += value.get();
			}

			// set output
			outputValue.set(sum);

			context.write(key, outputValue);

		}

	}

	/**
	 * Execute the command with the given arguments.
	 * 
	 * @param args
	 *            command specific arguments.
	 * @return exit code.
	 * @throws Exception
	 */
	// int run(String [] args) throws Exception;

	// step 3: Driver
	public int run(String[] args) throws Exception {

		Configuration configuration = this.getConf();

		Job job = Job.getInstance(configuration, this.getClass()
				.getSimpleName());
		job.setJarByClass(WCMapReduce.class);

		// set job
		// input
		Path inpath = new Path(args[0]);
		FileInputFormat.addInputPath(job, inpath);

		// output
		Path outPath = new Path(args[1]);
		FileOutputFormat.setOutputPath(job, outPath);

		// Mapper
		job.setMapperClass(WCMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		// ============shuffle=================
		// 1.partitioner
		// job.setPartitionerClass(cls);

		// 2.sort
		// job.setSortComparatorClass(cls);

		// 3.group
		// job.setGroupingComparatorClass(cls);

		job.setCombinerClass(WCCombiner.class);

		// ============shuffle=================

		// Reducer
		job.setReducerClass(WCReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// submit job -> YARN
		boolean isSuccess = job.waitForCompletion(true);
		return isSuccess ? 0 : 1;

	}

	public static void main(String[] args) throws Exception {

		Configuration configuration = new Configuration();

		args = new String[] {
				"hdfs://ns1/user/beifeng/wordcount/input/WordCountMap.txt",
				"hdfs://ns1/user/beifeng/wordcount/output10"};

		/*
		 * // run job int status = new WCMapReduce().run(args);
		 * 
		 * System.exit(status);
		 */
		// run job
		int status = ToolRunner.run(configuration, new WCMapReduce(), args);

		// exit program
		System.exit(status);
	}

}
