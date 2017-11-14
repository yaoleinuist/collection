package com.lzhsite.technology.thread.testLock;
//http://blog.csdn.net/vernonzheng/article/details/8288251
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/*reentrant 锁意味着什么呢？简单来说，它有一个与锁相关的获取计数器，如果拥有锁的某个线程再次得到锁，
 * 那么获取计数器就加1，然后锁需要被释放两次才能获得真正释放。这模仿了 synchronized 的语义；
 * 如果线程进入由线程已经拥有的监控器保护的 synchronized 块，就允许线程继续进行，当线程退出第二个（或者后续）
 *  synchronized 块的时候，不释放锁，只有线程退出它进入的监控器保护的第一个 synchronized 块时，才释放锁。

1.2 ReentrantLock与synchronized的比较

相同：ReentrantLock提供了synchronized类似的功能和内存语义。

不同：

（1）ReentrantLock功能性方面更全面，比如时间锁等候，可中断锁等候，锁投票等，因此更有扩展性。
在多个条件变量和高度竞争锁的地方，用ReentrantLock更合适，ReentrantLock还提供了Condition，
对线程的等待和唤醒等操作更加灵活，一个ReentrantLock可以有多个Condition实例，所以更有扩展性。

（2）ReentrantLock 的性能比synchronized会好点。

（3）ReentrantLock提供了可轮询的锁请求，他可以尝试的去取得锁，如果取得成功则继续处理，取得不成功，
可以等下次运行的时候处理，所以不容易产生死锁，而synchronized则一旦进入锁请求要么成功，要么一直阻塞，
所以更容易产生死锁。

*/

public class TestReentrantLock {

}
class ProductQueue<T> {  
	  
    private final T[] items;  
  
    private final Lock lock = new ReentrantLock();  
  
    private Condition notFull = lock.newCondition();  
  
    private Condition notEmpty = lock.newCondition();  
  
    //  
    private int head, tail, count;  
  
    public ProductQueue(int maxSize) {  
        items = (T[]) new Object[maxSize];  
    }  
  
    public ProductQueue() {  
        this(10);  
    }  
  
    public void put(T t) throws InterruptedException {  
        lock.lock();  
        try {  
            while (count == getCapacity()) {  
                notFull.await();  
            }  
            items[tail] = t;  
            if (++tail == getCapacity()) {  
                tail = 0;  
            }  
            ++count;  
            notEmpty.signalAll();  
        } finally {  
            lock.unlock();  
        }  
    }  
  
    public T take() throws InterruptedException {  
        lock.lock();  
        try {  
            while (count == 0) {  
                notEmpty.await();  
            }  
            T ret = items[head];  
            items[head] = null;//GC  
            //  
            if (++head == getCapacity()) {  
                head = 0;  
            }  
            --count;  
            notFull.signalAll();  
            return ret;  
        } finally {  
            lock.unlock();  
        }  
    }  
  
    public int getCapacity() {  
        return items.length;  
    }  
  
    public int size() {  
        lock.lock();  
        try {  
            return count;  
        } finally {  
            lock.unlock();  
        }  
    }  
  
}  