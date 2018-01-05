package com.lzhsite.technology.concurrent.testLinuxPerformance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * -agentpath:D:\tools\jprofiler6\bin\windows\jprofilerti.dll=port=25000
 * @author Administrator
 *
 */
public class MethodTime {
	static java.util.Random r=new java.util.Random();
	static Map<String,String> map=null;
	static{
		map=new HashMap<String,String>();
		map.put("1", "Java");
		map.put("2", "C++");
		map.put("3", "Delphi");
		map.put("4", "C");
		map.put("5", "Phython");
	}
	public String getNameById(String id){
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return map.get(id);
	}
	
	public List<String> getNamesByIds(String ids){
		List<String> re=new ArrayList<String>();
		String[] strs=ids.split(",");
		for(String id:strs){
			re.add(getNameById(id));
		}
		return re;
	}
	
	public List<String> getNamesByIdsBad(String ids){
		List<String> re=new ArrayList<String>();
		String[] strs=ids.split(",");
		for(String id:strs){
			//A bad code
			getNameById(id);
			re.add(getNameById(id));
		}
		return re;
	}
	
	public class NamesByIdsThread implements Runnable{
		@Override
		public void run() {
			try{
				while(true){
					int c=r.nextInt(4);
					String ids="";
					for(int i=0;i<c;i++)
						ids=Integer.toString((r.nextInt(4)+1))+",";
					getNamesByIds(ids);
				}
			}catch(Exception e){
			}
		}
	}
	
	public class NamesByIdsBadThread implements Runnable{
		@Override
		public void run() {
			try{
				while(true){
					int c=r.nextInt(4);
					String ids="";
					for(int i=0;i<c;i++)
						ids=Integer.toString((r.nextInt(4)+1))+",";
					getNamesByIdsBad(ids);
				}
			}catch(Exception e){
			}
		}
	}
	
	public static void main(String args[]){
		MethodTime instance=new MethodTime();
		new Thread(instance.new NamesByIdsThread()).start();
		new Thread(instance.new NamesByIdsBadThread()).start();
	}
}

