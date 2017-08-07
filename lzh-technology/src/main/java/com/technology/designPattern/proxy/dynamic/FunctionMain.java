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

public class FunctionMain {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		IDBQuery d = null;
		d = createJdkProxy();
		System.out.println(d.request());
		d = createCglibProxy();
		System.out.println(d.request());
		d = createJavassistDynProxy();
		System.out.println(d.request());
		d = createJavassistBytecodeDynamicProxy();
		System.out.println(d.request());
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
