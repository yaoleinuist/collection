package com.mapreduce;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
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
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class DistributedCacheWCMapReduce extends Configured implements Tool{

	/**
	 * Mapper 实现类
	 */
	public static class DistributedCacheWCMapper extends
			Mapper<LongWritable, Text, Text, IntWritable> {
		// cache
		List<String> list = new ArrayList<String>();

		private final static IntWritable mapOutputValue = new IntWritable(1);
		private Text mapOutputKey = new Text();
		
		@SuppressWarnings("deprecation")
		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {
			// step 1: get configuraiton
			Configuration conf = context.getConfiguration();
			// step 2: get cache uri
			URI[] uris = DistributedCache.getCacheFiles(conf);
			// step 3: path
			Path paht = new Path(uris[0]);
			// step 4: file system
			FileSystem fs = FileSystem.get(conf);
			// step 5: in stream
			InputStream inStream = fs.open(paht);
			// step 6: read data
			InputStreamReader isr = new InputStreamReader(inStream);
			BufferedReader bf = new BufferedReader(isr);
			String line ;
			while ((line = bf.readLine()) != null){
				if(line.trim().length() > 0){
					// add element
					list.add(line);
				}
			}
			bf.close();
			isr.close();
			inStream.close();
	//		fs.close();
		}

		@Override
		public void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			String lineValue = value.toString();
			StringTokenizer st = new StringTokenizer(lineValue);
			while(st.hasMoreTokens()){
				String wordValue = st.nextToken();
				
				if(list.contains(wordValue)){
					continue;
				}
				
				// set map output key
				mapOutputKey.set(wordValue);
				// output
				context.write(mapOutputKey, mapOutputValue);
			}
		}

		@Override
		public void cleanup(Context context) throws IOException,
				InterruptedException {
			super.cleanup(context);
		}

	}

	/**
	 * Reducer 实现类
	 */
	public static class DistributedCacheWCReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		public IntWritable outputValue = new IntWritable();
		
		@Override
		public void setup(Context context) throws IOException,
				InterruptedException {
			super.setup(context);
		}

		@Override
		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0 ;
			// iterator
			for(IntWritable value : values){
				sum += value.get() ;
			}
			// set 
			outputValue.set(sum);
			// job output 
			context.write(key, outputValue);
		}

		@Override
		public void cleanup(Context context) throws IOException,
				InterruptedException {
			super.cleanup(context);
		}
		
	}


	/**
	 * Driver :Job create,set,submit,run,monitor
	 */
	public int run(String[] args) throws Exception {
		// step 1:get configuration
		Configuration conf = this.getConf();
		
		// step 2:create job
		Job job = this.parseInputAndOutput(this,conf,args);
		
		// step 4:set job 
		
		// 2:mapper class
		job.setMapperClass(DistributedCacheWCMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class);
		
		// 4:reducer class
		job.setReducerClass(DistributedCacheWCReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		// step 5:submit job
		boolean isSuccess = job.waitForCompletion(true);
		
		return isSuccess ? 0 : 1;
	}
	
	public Job parseInputAndOutput(Tool tool,Configuration conf,String[] args) throws Exception {
	
		// validate args
		if(args.length != 2){
			System.err.println("Usage : " + tool.getClass().getSimpleName() + " [generic options] <input> <output>");
			ToolRunner.printGenericCommandUsage(System.err);
			return null;
		}
		
		// step 2:create job
		Job job = Job.getInstance(conf, tool.getClass().getSimpleName());
		
//		job.addCacheFile(uri);
		
		// step 3:set job run class
		job.setJarByClass(tool.getClass());
		
		// 1:input format
		Path inputPath = new Path(args[0]);
		FileInputFormat.addInputPath(job, inputPath);
		
		// 5:output format
		Path outputPath = new Path(args[1]);
		FileOutputFormat.setOutputPath(job, outputPath);
		
		return job;
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		
		args = new String[]{
				"hdfs://hadoop-yarn.beifeng.com:8020/user/beifeng/mr/distributedcache/input",//
				"hdfs://hadoop-yarn.beifeng.com:8020/user/beifeng/mr/distributedcache/ouput"
		};
		
		// mapreduce-default.xml,mapreduce-site.xml
		Configuration conf = new Configuration();
		
		// set distributed cache
		// ===============================================================
		URI uri = new URI("/user/beifeng/cachefile/cache.txt");
		DistributedCache.addCacheFile(uri, conf);
		// ===============================================================
		
		// run mapreduce
		int status = ToolRunner.run(conf, new DistributedCacheWCMapReduce(), args);
		
		// exit program
		System.exit(status);
	}

}
