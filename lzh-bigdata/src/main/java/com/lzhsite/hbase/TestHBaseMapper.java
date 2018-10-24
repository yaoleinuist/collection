package com.lzhsite.hbase;

import java.io.IOException;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;


/**
 * Mapper<ImmutableBytesWritable, Result, KEYOUT, VALUEOUT> 
 * ImmutableBytesWritable  ---> rowkey
 * Result --->Cell
 * 每一次一个map读一个rowkey，比如这里我们有6个rowkey，就是读取6次，每次处理是一个rowkey的所有数据（Cell）
 * 一个region对应一个maptask
 * @author ibf
 *
 */
public class TestHBaseMapper extends TableMapper<ImmutableBytesWritable, Put>{

	@Override
	protected void map(ImmutableBytesWritable key, Result value,
			Context context)
			throws IOException, InterruptedException {
		//封装Put对象，Put中封装了当前rowkey所对应的所有数据
		Put put = new Put(key.get());
		for(Cell cell : value.rawCells()){
			//判断当前列簇是否为info
			if("info".equals(Bytes.toString(CellUtil.cloneFamily(cell)))){
				//判断当前的cell是否为name
				if("name".equals(Bytes.toString(CellUtil.cloneQualifier(cell)))){
					//如果條件都匹配，就將cell放進去
					put.add(cell);
				}
			}
		}
		context.write(key, put);
	}
}
