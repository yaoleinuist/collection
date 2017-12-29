package com.lzhsite.mapreduce.sort;

import org.apache.hadoop.io.RawComparator;
import org.apache.hadoop.io.WritableComparator;

import com.lzhsite.mapreduce.io.PairWritable;

/**
 * RawComparator��hadoopΪ���л��ṩ���Ż��ӿ�
 * ����ֱ�ӱȽ��������еļ�¼�����跴���л�Ϊ����
 * @author lzhcode
 *
 */
public class FirstGroupingComparator implements RawComparator<PairWritable> {

	public int compare(PairWritable o1, PairWritable o2) {

		return o1.getFirst().compareTo(o2.getFirst());
	}

	public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {

		return WritableComparator.compareBytes(b1, 0, l1 - 4, b2, 0, l2 - 4);
	}

}
