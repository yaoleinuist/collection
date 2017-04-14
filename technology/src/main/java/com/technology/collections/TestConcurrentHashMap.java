package com.technology.collections;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class TestConcurrentHashMap {
/*    
 
             HashMap         Hashtable
父类                   AbstractMap     Dictiionary
是否同步            否                           是
k，v可否null  是                           否

 
HashMap是Hashtable的轻量级实现（非线程安全的实现），他们都完成了Map接口，

主要区别在于HashMap允许空（null）键值（key）,由于非线程安全，效率上可能高于Hashtable。
 
HashMap允许将null作为一个entry的key或者value，而Hashtable不允许。
 
HashMap把Hashtable的contains方法去掉了，改成containsvalue和containsKey。因为contains方法容易让人引起误解。 
 
Hashtable继承自Dictionary类，而HashMap是Java1.2引进的Map interface的一个实现。
 
最大的不同是，Hashtable的方法是Synchronize的，而HashMap不是，在多个线程访问Hashtable时，不需要自己为它的方法实现同步，
而HashMap 就必须为之提供外同步(Collections.synchronizedMap)。 


Hashtable,synchronized是针对整张Hash表的，即每次锁住整张表让线程独占，
ConcurrentHashMap允许多个修改操作并发进行，其关键在于使用了锁分离技术。
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

    public static void main(String[] args) throws InterruptedException {
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
}

/*
    ConcurrentHashMap与LinkedHashMap
	
	在JAVA中，LRU的原生实现是JDK中LinkedHashMap。LinkedHashMap继承自HashMap
	【实现原理】 简单说就是HashMap的每个节点做一个双向链表。
	每次访问这个节点，就把该节点移动到双向链表的头部。满了以后，就从链表的尾部删除。
	但是LinkedHashMap并是非线程安全（其实现中，双向链表的操作是没有任何线程安全的措施的）。
	对于线程安全的HashMap，在JDK中有ConcurrentHashMap原生支持。
	【实现原理】采用锁分离机制，把一个HashMap分成多个segement，
	对每个segement的写操作上锁。同时，他的get（）操作是没有锁的，
	具体思想就是把每个hash槽中的链表的头节点置成final的。对hash槽中链表操作，
	只能从头部去处理。这样就不会有读不一致的情况出现。这个原理，最好还是看源码，比较清晰。
*/