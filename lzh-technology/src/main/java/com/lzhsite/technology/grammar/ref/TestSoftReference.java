package com.lzhsite.technology.grammar.ref;

import java.lang.ref.SoftReference;

import javax.swing.ImageIcon;

 /**
  * 开发中，为了防止内存溢出，
  * 在处理一些占用内存大而且声明周期较长的对象时候，可以尽量应用软引用和弱引用技术。
  * @author lzhcode
  *
  */
public class TestSoftReference {
	
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
