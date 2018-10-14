package com.lzhsite.technology.concurrent.threadLocal;

import java.io.Closeable;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
/**
 * 分析过ThreadLocal源码源码的人都知道,ThreadLocal的设计的确巧妙,但是它也有一个缺陷:可能会引起内存泄漏;
 * ThreadLocalMap中key维护着一个weakReference,它在下次GC之前会被清理,如果Value仍然保持着外部的强引用,
 * 该ThreadLocal没有再进行set,get或者remove操作,时间长了就可能导致OutOfMemoryError .
 * lucene中的类CloseableThreadLocal对ThreadLocal做了处理,优化了其缺陷.学习一下:
 * 当执行CloseableThreadLocal.set(T)时，内部其实只是把值赋给内部的ThreadLocal对象，即执行ThreadLocal.set(new WeakReference(T)),将T包装成弱引用对象，
 * 目的就是当内存不足时，jvm可以回收此对象.
 * 引入一个新的问题:当前线程还存活着的时候，因为内存不足而回收了弱引用对象，这样会在下次调用get()时取不到值返回null，
 * 这是不可接受的.所以CloseableThreadLocal在内部还创建了一个私有的Map，WeakHashMap<Thread, T>，当线程只要存活时，则T就至少有一个引用存在，
 * 所以不会被提前回收。同时要注意另一个问题，要对WeakHashMap的操作使用synchronized做同步.
 * @author lzhcode
 * @param <T>
 */
public class CloseableThreadLocal<T> implements Closeable {
	//将值T用弱引用包裹,
  private ThreadLocal<WeakReference<T>> t = new ThreadLocal<>();

  // Use a WeakHashMap so that if a Thread exits and is
  // GC'able, its entry may be removed:
  //使用WeakHashMap，这样如果一个Thread退出并且是可GC，它Entry可能将被删除：
  //亦即WeakHashMap<Thread, T>，当线程只要存活时，则T就至少有一个引用存在，所以不会被提前回收
  private Map<Thread,T> hardRefs = new WeakHashMap<>();
  
  // Increase this to decrease frequency of purging in get:
  private static int PURGE_MULTIPLIER = 20;

  // On each get or set we decrement this; when it hits 0 we
  // purge.  After purge, we set this to
  // PURGE_MULTIPLIER * stillAliveCount.  This keeps
  // amortized cost of purging linear.
  private final AtomicInteger countUntilPurge = new AtomicInteger(PURGE_MULTIPLIER);

  protected T initialValue() {
    return null;
  }
  
  public T get() {
    WeakReference<T> weakRef = t.get();
    if (weakRef == null) {
      T iv = initialValue();
      if (iv != null) {
        set(iv);
        return iv;
      } else {
        return null;
      }
    } else {
      maybePurge();
      return weakRef.get();
    }
  }

  public void set(T object) {

    t.set(new WeakReference<>(object));
	//使用synchronized同步
    synchronized(hardRefs) {
      hardRefs.put(Thread.currentThread(), object);
      maybePurge();
    }
  }

  private void maybePurge() {
    if (countUntilPurge.getAndDecrement() == 0) {
      purge();
    }
  }

  // Purge dead threads
  private void purge() {
    synchronized(hardRefs) {
      int stillAliveCount = 0;
      for (Iterator<Thread> it = hardRefs.keySet().iterator(); it.hasNext();) {
        final Thread t = it.next();
        if (!t.isAlive()) {
          it.remove();
        } else {
          stillAliveCount++;
        }
      }
      int nextCount = (1+stillAliveCount) * PURGE_MULTIPLIER;
      if (nextCount <= 0) {
        // defensive: int overflow!
        nextCount = 1000000;
      }
      
      countUntilPurge.set(nextCount);
    }
  }

  @Override
  public void close() {
    // Clear the hard refs; then, the only remaining refs to
    // all values we were storing are weak (unless somewhere
    // else is still using them) and so GC may reclaim them:
    hardRefs = null;
    // Take care of the current thread right now; others will be
    // taken care of via the WeakReferences.
    if (t != null) {
      t.remove();
    }
    t = null;
  }
}
 