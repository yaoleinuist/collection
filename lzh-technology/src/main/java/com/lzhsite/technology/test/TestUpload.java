package com.lzhsite.technology.test;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.lzhsite.util.HttpUtils;

public class TestUpload {

	
	public static void main(String[] args) {
		
		try {
			
 		  Map<String, String> params =new HashMap<String, String>();
 		  params.put("userName", "18046049715");
 		  String result= HttpUtils.postUrlAsString("http://localhost/uploadFile/copyFile?id=252", params);
 	  
//		  String  result="{'data':{'id':291,'vid':'C93C1F205FE2E3E80CEACF8046153DD924976A67D200181A21E74E6A84184231'},'id':0,'status':0,'timeout':0,'dataType':'SUCCESS_FILEMANAGER_1'}";
  
          JSONObject jsonObject= new JSONObject(result);
		  
		  System.out.println(new JSONObject(jsonObject.get("data").toString()).get("id"));
		  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
