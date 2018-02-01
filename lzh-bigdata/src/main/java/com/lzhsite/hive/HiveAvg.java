package com.lzhsite.hive;

import org.apache.hadoop.hive.ql.exec.UDAF;
import org.apache.hadoop.hive.ql.exec.UDAFEvaluator;
import org.apache.hadoop.hive.serde2.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;

public class HiveAvg extends UDAF {
	public static class AvgEvaluate implements UDAFEvaluator {
		public static class PartialResult {
			public int count;
			public double total;

			public PartialResult() {
				count = 0;
				total = 0;
			}

		}

		private PartialResult partialResult;

		@Override
		public void init() {
			partialResult = new PartialResult();
		}

		public boolean iterate(IntWritable value) {
			// 此处一定要判断partialResult是否为空，否则会报错
			// 原因就是init函数只会被调用一遍，不会为每个部分聚集操作去做初始化
			// 此处如果不加判断就会出错
			if (partialResult == null) {
				partialResult = new PartialResult();
			}

			if (value != null) {
				partialResult.total = partialResult.total + value.get();
				partialResult.count = partialResult.count + 1;
			}

			return true;
		}

		public PartialResult terminatePartial() {
			return partialResult;
		}

		public boolean merge(PartialResult other) {
			partialResult.total = partialResult.total + other.total;
			partialResult.count = partialResult.count + other.count;

			return true;
		}

		public DoubleWritable terminate() {
			return new DoubleWritable(partialResult.total / partialResult.count);
		}
	}
}

//add jar/home/user/hadoop_jar/hiveudf.jar; 
//create temporary function avg_udf as'com.oserp.hiveudf.HiveAvg';
//select classNo, avg_udf(score) from student group by classNo;  
//输出结果如下：
//C01 68.66666666666667
//C02 80.66666666666667
//C03 73.33333333333333

//参照以上图示（来自Hadoop权威教程）我们来看看各个函数：
//   Init在类似于构造函数，用于UDF的初始化。
//   注意上图中红色框中的init函数。在实际运行中，无论hive将记录集划分了多少个部分去做（比如上图中的file1和file2两个部分），init函数仅被调用一次。所以上图中的示例是有歧义的。这也是为什么上面的代码中加了特别的注释来说明。或者换一句话说，init函数中不应该用于初始化部分聚集值相关的逻辑，而应该处理全局的一些数据逻辑。
//   Iterate函数用于聚合。当每一个新的值被聚合时，此函数被调用。
//   TerminatePartial函数在部分聚合完成后被调用。当hive希望得到部分记录的聚合结果时，此函数被调用。
//   Merge函数用于合并先前得到的部分聚合结果（也可以理解为分块记录的聚合结果）。
//   Terminate返回最终的聚合结果。
// 
//我们可以看出merge的输入参数类型和terminatePartial函数的返回值类型必须是一致的。