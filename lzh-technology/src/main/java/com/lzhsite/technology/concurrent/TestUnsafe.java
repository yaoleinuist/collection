package com.lzhsite.technology.concurrent;

import java.lang.reflect.Field;
import sun.misc.Unsafe;


public class TestUnsafe {
	
	    public static void main(String[] args) {
	        Node node = new Node();
	        /**
	         * 通过CAS方法更新node的next属性
	         * 原子操作
	         */
	        boolean flag = node.casNext(null,new Node());
	        System.out.println(flag);
	    }

	    private static class Node{

	        volatile Node next;

	        /**
	         * 使用Unsafe CAS方法
	         * @param cmp 目标值this与cmp比较，如果相等就更新返回true；如果不相等就不更新返回false；
	         * @param val 需要更新的值；
	         * @return
	         */
	        boolean casNext(Node cmp, Node val) {
	            /**
	             * compareAndSwapObject(Object var1, long var2, Object var3, Object var4)
                 * 第一个参数是要修改的对象
                 * 第二个参数是对象中要修改变量的偏移量
                 * 第三个参数是修改之前的值
                 * 第四个参数是预想修改后的值.
	             */
	            return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
	        }

	        private static final sun.misc.Unsafe UNSAFE;
	        private static final long nextOffset;

	        static {
	            try {
	                UNSAFE = getUnsafe();
	                Class<?> k = Node.class;
	                nextOffset = UNSAFE.objectFieldOffset
	                        (k.getDeclaredField("next"));
	            } catch (Exception e) {
	                throw new Error(e);
	            }
	        }

	        /**
	         * 获取Unsafe的方法
	         * 获取了以后就可以愉快的使用CAS啦
	         * @return
	         */
	        public static Unsafe getUnsafe() {
	            try {
	                Field f = Unsafe.class.getDeclaredField("theUnsafe");
	                f.setAccessible(true);
	                return (Unsafe)f.get(null);
	            } catch (Exception e) {
	                return null;
	            }
	        }
	    }
}
