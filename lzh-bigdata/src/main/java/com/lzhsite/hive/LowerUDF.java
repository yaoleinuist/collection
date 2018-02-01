package com.lzhsite.hive;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * 1. Implement one or more methods named
 * "evaluate" which will be called by Hive.
 * 
 * 2. "evaluate" should never be a void method. 
 * However it can return "null" if needed.
 * @author XuanYu
 *
 */
public class LowerUDF extends UDF {
	
	public Text evaluate(Text str){
		// validate 
		if(null == str.toString()){
			return null ;
		}
		// lower
		return new Text (str.toString().toLowerCase())  ;
	}
	
	public static void main(String[] args) {
		System.out.println(new LowerUDF().evaluate(new Text("HIVE")));
	}

}
