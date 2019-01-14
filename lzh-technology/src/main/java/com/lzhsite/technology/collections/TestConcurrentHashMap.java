package com.lzhsite.technology.collections;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Test;

public class TestConcurrentHashMap {
/**
 *  ConcurrentHashMap
	【实现原理】允许多个修改操作并发进行，其关键在于使用了锁分离技术。
	它使用了多个锁来控制对hash表的不同部分进行的修改。
	ConcurrentHashMap内部使用段(Segment)来表示这些不同的部分，每个段其实就是一个小的hash table，它们有自己的锁。
	只要多个修改操作发生在不同的段上，它们就可以并发进行。
	有些方法需要跨段，比如size()和containsValue()，它们可能需要锁定整个表而而不仅仅是某个段，
	这需要按顺序锁定所有段，操作完毕后，又按顺序释放所有段的锁。
	这里“按顺序”是很重要的，否则极有可能出现死锁，在ConcurrentHashMap内部，段数组是final的，并且其成员变量实际上也是final的，
	但是，仅仅是将数组声明为final的并不保证数组成员也是final的，这需要实现上的保证。
	这可以确保不会出现死锁，因为获得锁的顺序是固定的。
	

	
	Hash表的一个很重要方面就是如何解决hash冲突，ConcurrentHashMap 和HashMap使用相同的方式，
	都是将hash值相同的节点放在一个hash链中。与HashMap不同的是，ConcurrentHashMap使用多个子Hash表，也就是段(Segment)。
	 
	ConcurrentHashMap允许一边更新、一边遍历，也就是说在Iterator对象遍历的时候，
	ConcurrentHashMap也可以进行remove,put操作，且遍历的数据会随着remove,put操作产出变化
	
	Hashtable在使用iterator遍历的时候，如果其他线程，
	包括本线程对Hashtable进行了put，remove等更新操作的话，
	就会抛出ConcurrentModificationException异常，但如果使用ConcurrentHashMap的话，就不用考虑这方面的问题了
	一个线程对ConcurrentHashMap增加数据，另外一个线程在遍历时就能获得。
*/
	static Map<Long, String> conMap = new ConcurrentHashMap<Long, String>();

    @Test
    public void test1() throws InterruptedException{
        for (long i = 0; i < 5; i++) {
            conMap.put(i, i + "");
        }

        Thread thread = new Thread(new Runnable() {
            public void run() {
                conMap.put(100l, "100");
                System.out.println(Thread.currentThread().getName()+" ADD:" + 100);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
        Thread thread2 = new Thread(new Runnable() {
            public void run() {
                for (Iterator<Entry<Long, String>> iterator = conMap.entrySet().iterator(); iterator.hasNext();) {
                    Entry<Long, String> entry = iterator.next();
                    System.out.println(Thread.currentThread().getName()+" "+entry.getKey() + " - " + entry.getValue());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        thread2.start();

        Thread.sleep(3000);
        System.out.println("--------");
        for (Entry<Long, String> entry : conMap.entrySet()) {
            System.out.println(Thread.currentThread().getName()+" "+entry.getKey() + " " + entry.getValue());
        }

    }
    
    /**
     *  java8之前。从map中根据key获取value操作可能会有下面的操作
		Object key = map.get("key");
		if (key == null) {
		    key = new Object();
		    map.put("key", key);
		}
		
		java8之后。上面的操作可以简化为一行，若key对应的value为空，会将第二个参数的返回值存入并返回
		Object key2 = map.computeIfAbsent("key", k -> new Object());
		
        computeIfAbsent也有可能会造成CPU 100%的异常现象。这个怪异现象存在于JDK8的ConcurrentHashMap中，
                 在JDK9中已经得到修复。问题的关键在于递归使用了computeIfAbsent方法
     */
    @Test
    public void test2(){
    	Map<String, String> map = new ConcurrentHashMap<>();

    	System.out.println(map.computeIfAbsent("AaAa", key -> map.computeIfAbsent("BBBB", key2 -> "value")));
    }
}

 