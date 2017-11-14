package com.lzhsite.technology.quto;

import java.lang.ref.SoftReference;

import javax.swing.ImageIcon;

/*
 * Java中的引用类型有4种，由强到弱依次如下：

    1) 强引用（StrongReference）是使用最普遍的引用，类似：“Object obj = new Object()” 。如果一个对象具有强引用，
               那垃圾回收器绝不会回收它。当内存空间不足，Java虚拟机宁愿抛出OutOfMemoryError错误，使程序异常终止，也不会靠随意回收具有强
              引用的对象来解决内存不足的问题。

	2) 软引用（Soft Reference）是用来描述一些有用但并不是必需的对象，如果内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，
	就会回收这些对象的内存。只要垃圾回收器没有回收它，该对象就可以被程序使用。软引用可用来实现内存敏感的高速缓存。
	
	3) 弱引用（WeakReference）也是用来描述非必需对象的，但是它的强度比软引用更弱一些，被弱引用关联的对象只能生存到下一次垃圾收集发生之前，
	当垃圾收集器工作时，无论当前内存是否足够，都会回收掉只被弱引用关联的对象。不过，由于垃圾回收器是一个优先级很低的线程，因此不一定会很快发现那些
	只具有弱引用的对象。
	
	4) 虚引用（PhantomReference）也称为幽灵引用或者幻影引用，它是最弱的一种引用关系，一个对象是否有虚引用的存在，完全不会对其生存时间构成影响，
	也无法通过虚引用来取得一个对象实例。*/
public class Test {
	public static void main(String[] args) {
		// 申请一个图像对象
		ImageIcon image = new ImageIcon(); // 创建Image对象
		// 使用 image …
		// 使用完了image，将它设置为soft 引用类型，并且释放强引用；
		SoftReference sr = new SoftReference(image);
		image = null;

		// 下次使用时
		if (sr != null)
			image = (ImageIcon) sr.get();
		else {
			image = new ImageIcon(); // 由于GC由于低内存，已释放image，因此需要重新装载；
			sr = new SoftReference(image);
		}
	}
}
