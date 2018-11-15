package com.lzhsite.technology.collections.hashMap.myHashMap;

import java.util.ArrayList;
import java.util.List;

public class MyHashMap<K,V> {
	
	
	private static int defaulLenth= 1<<4; //默认的整数倍,可以大大减少hash冲突,
	private static double defaulAddSizeFactory = 0.75;
	private Entry<K, V> [] data;
	private int capacity;  //最开始的大小,扩容之前
	private int size;
	

	public MyHashMap() {
		this(defaulLenth,defaulAddSizeFactory);
	}
	public MyHashMap(int capacity) {
		this(capacity,defaulAddSizeFactory);
	}
	
	public MyHashMap(int capacity,double defaulAddSizeFactory) {
		if(capacity > 0){
			data =new Entry[capacity];
			size = 0;
			this.capacity =capacity;
			this.defaulAddSizeFactory =defaulAddSizeFactory;
		}else{
			System.out.println("error");
		}
		
	}


	//计算hash值
	private int hash(K key) {
		int h= 0;
		if(key ==null) h=0;
		else{
			h= key.hashCode() ^ (h>>>16);
		}
		return h % defaulLenth;
	}

	public V get(K key){
		int hash =hash(key);
		Entry<K, V> entry = data[hash];
	
		//链表遍历
		while (entry !=null) {
		  if(entry.key.equals(key)){
			  return entry.value;
		  }
		  entry = entry.next;
		}
		
		return null;
	}
	
	public int size(){
	   return size;	
	}
	
	public Boolean isEmpty(){
		   return size==0;	
	}
	
	public void put(K key,V value){
		if(key==null){
			System.out.println("error");
		}
		if(capacity > defaulLenth * defaulAddSizeFactory){
			up2Size();
		}
		//不同的key的hash一样时(hash冲突),新进的数据存在同一个数组下标的链表下
		int hash =hash(key);
		Entry<K, V> nE = new Entry<K, V>(key, value, null);
		Entry<K, V> mE = data[hash];
		//链表遍历
		while (mE !=null) {
		  if(mE.key.equals(key)){
			  mE.value=value;
			  return;
		  }
		  mE = mE.next;
		}
		//构造新链表(此处的data[hash]代表原来的链表)
		nE.next = data[hash];
		data[hash] = nE;
		size++;
	}

	//扩容2倍
	private void up2Size() {
		// TODO Auto-generated method stub
		Entry<K, V>[] newdata = new Entry[2 * defaulLenth];
	    againHash(newdata);
	}

	//将存储的对象散列到新数组
	private void againHash(Entry<K, V>[] newdata) {
		// TODO Auto-generated method stub
		//数组的对象封装到list
		 List<Entry<K, V>> entryList =new ArrayList<Entry<K,V>>();
		 for (int i = 0; i < data.length; i++) {
			if(data[i]==null){
				continue;
			}
			foundEntryByNext(data[i],entryList);
		}
		 if(entryList.size()>0){
			 size = 0;
			 defaulLenth =2 * defaulLenth;
			 data = newdata ;
			 for (Entry<K,V> entry:entryList) {
				if(entry.next!=null){
					//重新散列之前的形成链表关系取消
					entry.next =null;
				}
				put(entry.key,entry.value);
			}
		 }
	}
	
	//寻找Entry对象
	private void foundEntryByNext(Entry<K, V> entry,  List<MyHashMap<K, V>.Entry<K, V>> entryList) {
		// TODO Auto-generated method stub
		if(entry !=null && entry.next!=null){
			//这个entry形成链表结构
			entryList.add(entry);
			foundEntryByNext(entry.next, entryList);
		}else if(entry !=null){
			entryList.add(entry);
		}
	}

	private class Entry<K,V>{
		K key;
		V value;
	    Entry<K,V> next;
		
	    public Entry(K key, V value,  Entry<K, V> next) {
			super();
			this.key = key;
			this.value = value;
			this.next = next;
		}
		 
		
	}
	
	
	public static void main(String[] args) {
		MyHashMap map=new MyHashMap<>();
		map.put("1", 1);
		map.put("2", 2);
		map.put("3", 3);
		map.put("4", 4);
		
		System.out.println(map.get("4"));
	}

}
