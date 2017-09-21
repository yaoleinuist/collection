package com.mapreduce.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
/**
 * 	1、MR中的所有数据类型都统一的实现了writable的接口
 *	2、当程序间传递对象或者持久化对象的时候，就需要序列化对象成字节流
 *	3、writable就是Hadoop中序列化的格式
 * @author lzhcode
 *
 */
public class UserWritable implements Writable {

	private int id;
	private String name;

	public UserWritable() {

	}

	public UserWritable(int id, String name) {
		this.set(id, name);
	}

	public void set(int id, String name) {

		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void write(DataOutput out) throws IOException {
		out.writeInt(id);
		out.writeUTF(name);

	}

	public void readFields(DataInput in) throws IOException {
		this.id = in.readInt();
		this.name = in.readUTF();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserWritable other = (UserWritable) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return id + "\t" + name;
	}

}
