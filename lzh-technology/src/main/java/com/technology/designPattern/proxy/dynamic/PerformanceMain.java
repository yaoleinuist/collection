package com.technology.designPattern.proxy.dynamic;

import java.lang.reflect.Proxy;

import com.technology.designPattern.proxy.IDBQuery;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;
import net.sf.cglib.proxy.Enhancer;

public class PerformanceMain {
	public static final int CIRCLE = 30000000;

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		IDBQuery d = null;
		long begin = System.currentTimeMillis();
		d = createJdkProxy();
		System.out.println("createJdkProxy:" + (System.currentTimeMillis() - begin));
		System.out.println("JdkProxy class:" + d.getClass().getName());
		begin = System.currentTimeMillis();
		for (int i = 0; i < CIRCLE; i++)
			d.request();
		System.out.println("callJdkProxy:" + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
		d = createCglibProxy();
		System.out.println("createCglibProxy:" + (System.currentTimeMillis() - begin));
		System.out.println("CglibProxy class:" + d.getClass().getName());
		begin = System.currentTimeMillis();
		for (int i = 0; i < CIRCLE; i++)
			d.request();
		System.out.println("callCglibProxy:" + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
		d = createJavassistDynProxy();
		System.out.println("createJavassistDynProxy:" + (System.currentTimeMillis() - begin));
		System.out.println("JavassistDynProxy class:" + d.getClass().getName());
		begin = System.currentTimeMillis();
		for (int i = 0; i < CIRCLE; i++)
			d.request();
		System.out.println("callJavassistDynProxy:" + (System.currentTimeMillis() - begin));

		begin = System.currentTimeMillis();
		d = createJavassistBytecodeDynamicProxy();
		System.out.println("createJavassistBytecodeDynamicProxy:" + (System.currentTimeMillis() - begin));
		System.out.println("JavassistBytecodeDynamicProxy class:" + d.getClass().getName());
		begin = System.currentTimeMillis();
		for (int i = 0; i < CIRCLE; i++)
			d.request();
		System.out.println("callJavassistBytecodeDynamicProxy:" + (System.currentTimeMillis() - begin));
	}

	public static IDBQuery createJdkProxy() {
		IDBQuery jdkProxy = (IDBQuery) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
				new Class[] { IDBQuery.class }, new JdkDbQeuryHandler());
		return jdkProxy;
	}

	public static IDBQuery createCglibProxy() {
		Enhancer enhancer = new Enhancer();
		enhancer.setCallback(new CglibDbQueryInterceptor());
		enhancer.setInterfaces(new Class[] { IDBQuery.class });
		IDBQuery cglibProxy = (IDBQuery) enhancer.create();
		return cglibProxy;
	}

	public static IDBQuery createJavassistDynProxy() throws Exception {
		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.setInterfaces(new Class[] { IDBQuery.class });
		Class proxyClass = proxyFactory.createClass();
		IDBQuery javassistProxy = (IDBQuery) proxyClass.newInstance();
		((ProxyObject) javassistProxy).setHandler(new JavassistDynDbQueryHandler());
		return javassistProxy;
	}

	public static IDBQuery createJavassistBytecodeDynamicProxy() throws Exception {
		ClassPool mPool = new ClassPool(true);
		CtClass mCtc = mPool.makeClass(IDBQuery.class.getName() + "JavaassistBytecodeProxy");
		mCtc.addInterface(mPool.get(IDBQuery.class.getName()));
		mCtc.addConstructor(CtNewConstructor.defaultConstructor(mCtc));
		mCtc.addField(CtField.make("public " + IDBQuery.class.getName() + " real;", mCtc));
		String dbqueryname = DBQuery.class.getName();
		mCtc.addMethod(CtNewMethod.make(
				"public String request() { if(real==null)real=new " + dbqueryname + "();return real.request(); }",
				mCtc));
		Class pc = mCtc.toClass();
		IDBQuery bytecodeProxy = (IDBQuery) pc.newInstance();
		return bytecodeProxy;
	}

}
