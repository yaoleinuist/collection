package com.lzhsite.hive;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * 1. Implement one or more methods named "evaluate" which will be called by
 * Hive.
 * 
 * 2. "evaluate" should never be a void method. However it can return "null" if
 * needed.
 * 
 * @author XuanYu
 *
 */
public class DateTransformUDF extends UDF {

	private final SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.ENGLISH);
	private final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	/**
	 * 31/Aug/2015:00:04:37 +0800
	 * 
	 * 20150831000437
	 * 
	 * @param str
	 * @return
	 */
	public Text evaluate(Text input) {
		Text output = new Text();
		
		// validate
		if (null == input) {
			return null;
		}
		
		String inputDate = input.toString().trim();
		if(null == inputDate){
			return null ;
		}
		
		try{
			
			// parse
			Date parseDate = inputFormat.parse(inputDate);
			
			//transform
			String outputDate = outputFormat.format(parseDate);
			
			// set
			output.set(outputDate);
			
		}catch(Exception e){
			e.printStackTrace();
			return output ;
		}
		
		return output;
	}

	public static void main(String[] args) {
		System.out.println(new DateTransformUDF().evaluate(new Text("31/Aug/2015:00:04:37 +0800")));
	}

}
