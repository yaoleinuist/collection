package com.mapreduce.sort;

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

public class SortMapReduce extends Configured implements Tool {

	// step 1: Mapper
	public static class SortMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		private Text mapOutputKey = new Text();
		private IntWritable mapOutputValue = new IntWritable();

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {

			// line value
			String lineValue = value.toString();

			// spilt
			String[] strs = lineValue.split(" ");

			if (2 != strs.length) {
				return;
			}

			// set mapoutput key
			mapOutputKey.set(strs[0]);
			mapOutputValue.set(Integer.valueOf(strs[1]));

			context.write(mapOutputKey, mapOutputValue);
		}

	}

	// step 2: Reducer
	public static class SortReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {

		@Override
		protected void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {

			/**
			 * List<IntWritable> valueList = new ArrayList<IntWritable>();
			 * 
			 * for (IntWritable value : values) { valueList.add(value); }
			 * 
			 * Collections.sort(valueList);
			 */

			// iterator
			for (IntWritable value : values) {
				context.write(key, value);
			}
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
		job.setJarByClass(SortMapReduce.class);

		// set job
		// input
		Path inpath = new Path(args[0]);
		FileInputFormat.addInputPath(job, inpath);

		// output
		Path outPath = new Path(args[1]);
		FileOutputFormat.setOutputPath(job, outPath);

		// Mapper
		job.setMapperClass(SortMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);

		// ============shuffle=================
		// 1.partitioner
		// job.setPartitionerClass(cls);

		// 2.sort
		// job.setSortComparatorClass(cls);

		// 3.group
		// job.setGroupingComparatorClass(cls);

		// job.setCombinerClass(WCCombiner.class);

		// ============shuffle=================

		// Reducer
		job.setReducerClass(SortReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		// submit job -> YARN
		boolean isSuccess = job.waitForCompletion(true);
		return isSuccess ? 0 : 1;

	}

	public static void main(String[] args) throws Exception {

		Configuration configuration = new Configuration();

		args = new String[] {
				"hdfs://ns1/user/beifeng/sort/input/sort.txt",
				"hdfs://ns1/user/beifeng/sort/output" };

		/*
		 * // run job int status = new WCMapReduce().run(args);
		 * 
		 * System.exit(status);
		 */
		// run job
		int status = ToolRunner.run(configuration, new SortMapReduce(), args);

		// exit program
		System.exit(status);
	}

}
