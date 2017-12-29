package com.lzhsite.mapreduce.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
/**
 * 	4、比较-》比较器
 *	5、comparable就是一个排序接口，如果一个类实现了它，就代表着这个类支持排序
 *	6、两个对象比较-》两个值比较
 * @author lzhcode
 *
 */
public class OrderWritable implements WritableComparable<OrderWritable> {

	private String orderId;
	private float price;

	public OrderWritable() {

	}

	public OrderWritable(String orderId, float price) {
		this.set(orderId, price);
	}

	public void set(String orderId, float price) {
		this.orderId = orderId;
		this.price = price;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public void write(DataOutput out) throws IOException {
		out.writeUTF(orderId);
		out.writeFloat(price);

	}

	public void readFields(DataInput in) throws IOException {

		this.orderId = in.readUTF();
		this.price = in.readFloat();
	}

	public int compareTo(OrderWritable o) {

		int comp = this.getOrderId().compareTo(o.getOrderId());

		if (0 == comp) {
			return Float.valueOf(this.getPrice()).compareTo(
					Float.valueOf(o.getPrice()));
		}

		return comp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((orderId == null) ? 0 : orderId.hashCode());
		result = prime * result + Float.floatToIntBits(price);
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
		OrderWritable other = (OrderWritable) obj;
		if (orderId == null) {
			if (other.orderId != null)
				return false;
		} else if (!orderId.equals(other.orderId))
			return false;
		if (Float.floatToIntBits(price) != Float.floatToIntBits(other.price))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return orderId + "\t" + price;
	}

}
