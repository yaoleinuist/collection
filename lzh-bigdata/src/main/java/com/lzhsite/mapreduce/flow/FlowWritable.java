package com.lzhsite.mapreduce.flow;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
/**
 * Writable-->hadoop的序列化接口，自定义value类型一般实现此接口
 * 自定义key实现WritableComparable
 * @author Administrator
 *
 */

public class FlowWritable implements Writable{
	private long up_flow;  //上行流量
	private long down_flow; //下行流量
	private long sum_flow; //总流量
	

	public FlowWritable() {
		
	}

	public FlowWritable(long up_flow, long down_flow, long sum_flow) {
		super();
		this.up_flow = up_flow;
		this.down_flow = down_flow;
		this.sum_flow = sum_flow;
	}

	public long getUp_flow() {
		return up_flow;
	}

	public void setUp_flow(long up_flow) {
		this.up_flow = up_flow;
	}

	public long getDown_flow() {
		return down_flow;
	}

	public void setDown_flow(long down_flow) {
		this.down_flow = down_flow;
	}

	public long getSum_flow() {
		return sum_flow;
	}

	public void setSum_flow(long sum_flow) {
		this.sum_flow = sum_flow;
	}
	
	//序列化  -> 便于在网络中传输数据（将对象转化成二进制）
	public void write(DataOutput out) throws IOException {
		out.writeLong(up_flow);
		out.writeLong(down_flow);
		out.writeLong(sum_flow);
		
	}
	
	//反序列 -> 将二进制转化成对象
	public void readFields(DataInput in) throws IOException {
		this.up_flow = in.readLong();
		this.down_flow = in.readLong();
		this.sum_flow = in.readLong();				
	}

	@Override
	public String toString() {	
		return up_flow+"\t"+down_flow+"\t"+sum_flow;
	}
	
	

}
