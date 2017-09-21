package com.mapreduce.sort;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

import com.mapreduce.io.PairWritable;

public class FirstPartitioner extends Partitioner<PairWritable, IntWritable> {

	@Override
	public int getPartition(PairWritable key, IntWritable value,
			int numPartitions) {
		return (key.getFirst().hashCode() & Integer.MAX_VALUE) % numPartitions;
	}

}
