package com.technology.jvm.classLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import org.junit.Test;

import com.technology.util.HttpUtils;

/**
 * http://blog.csdn.net/xyang81/article/details/7292380#NetWorkClassLoader
 * 为什么要使用双亲委托这种模型呢? 因为这样可以避免重复加载，当父亲已经加载了该类的时候，就没有必要子ClassLoader再加载一次。
 * 考虑到安全因素，我们试想一下，如果不使用这种委托模式，那我们就可以随时使用自定义的String来动态替代java核心api中定义的类型，
 * 这样会存在非常大的安全隐患，而双亲委托的方式，就可以避免这种情况，因为String已经在启动时就被引导类加载器（Bootstrcp
 * ClassLoader）加载， 所以用户自定义的ClassLoader永远也无法加载一个自己写的String，
 * 除非你改变JDK中ClassLoader搜索类的默认算法
 */
public class TestClassLoader {

	/**
	 * BootStrap ClassLoader：称为启动类加载器，是Java类加载层次中最顶层的类加载器，
	 * 负责加载JDK中的核心类库，如：rt.jar、resources.jar、charsets.jar等，
	 * 可通过如下程序获得该类加载器从哪些地方加载了相关的jar或class文件
	 */
	@Test
	public void Test1() {

		URL[] urls = sun.misc.Launcher.getBootstrapClassPath().getURLs();
		for (int i = 0; i < urls.length; i++) {
			System.out.println(urls[i].toExternalForm());
		}
		System.out.println(System.getProperty("sun.boot.class.path"));
	}

	/**
	 * Bootstrap ClassLoader：不继承自ClassLoader，因为它不是一个普通的Java类，底层由C++编写，
	 * 已嵌入到了JVM内核当中，当JVM启动后，Bootstrap ClassLoader也随着启动， 负责加载完核心类库后，并构造Extension
	 * ClassLoader和App ClassLoader类加载器。
	 * 
	 * Extension
	 * ClassLoader：称为扩展类加载器，负责加载Java的扩展类库，默认加载JAVA_HOME/jre/lib/ext/目下的所有jar。
	 * App ClassLoader：称为系统类加载器，负责加载应用程序classpath目录下的所有jar和class文件
	 * 
	 * 第一行结果说明：ClassLoaderTest的类加载器是AppClassLoader。
	 * 第二行结果说明：AppClassLoader的类加器是ExtClassLoader，即parent=ExtClassLoader。
	 * 第三行结果说明：ExtClassLoader的类加器是Bootstrap ClassLoader，因为Bootstrap
	 * ClassLoader不是一个普通的Java类，
	 * 所以ExtClassLoader的parent=null，所以第三行的打印结果为null就是这个原因。
	 * 
	 * 将ClassLoaderTest.class打包成ClassLoaderTest.jar，放到Extension
	 * ClassLoader的加载目录下（JAVA_HOME/jre/lib/ext），然后重新运行这个程序，得到的结果会是什么样呢？ 打印结果：
	 * sun.misc.Launcher$ExtClassLoader@f2a0b8e null 打印结果分析：
	 * 为什么第一行的结果是ExtClassLoader呢？
	 * 因为ClassLoader的委托模型机制，当我们要用ClassLoaderTest.class这个类的时候，AppClassLoader在试图加载之前，先委托给Bootstrcp
	 * ClassLoader，Bootstracp
	 * ClassLoader发现自己没找到，它就告诉ExtClassLoader，兄弟，我这里没有这个类，你去加载看看，然后Extension
	 * ClassLoader拿着这个类去它指定的类路径（JAVA_HOME/jre/lib/ext）试图加载，唉，它发现在ClassLoaderTest.jar这样一个文件中包含ClassLoaderTest.class这样的一个文件，然后它把找到的这个类加载到内存当中，并生成这个类的Class实例对象，最后把这个实例返回。所以ClassLoaderTest.class的类加载器是ExtClassLoader。
	 * 第二行的结果为null，是因为ExtClassLoader的父类加载器是Bootstrap ClassLoader。
	 */
	@Test
	public void Test2() {
		ClassLoader loader = TestClassLoader.class.getClassLoader(); // 获得ClassLoaderTest这个类的类加载器
		while (loader != null) {
			System.out.println(loader);
			loader = loader.getParent(); // 获得父加载器的引用
		}
		System.out.println(loader);
	}

	/**
	 * JVM在判定两个class是否相同时，不仅要判断两个类名是否相同，而且要判断是否由同一个类加载器实例加载的。
	 * 只有两者同时满足的情况下，JVM才认为这两个class是相同的。
	 *
	 * 因为虚拟机中存在了两个MyClassLoaderTest类，一个是由系统应用程序类加载器加载的，
	 * 另外一个是由我们自定义的类加载器加载的，虽然都来自同一个Class文件， 但依然是两个独立的类，做对象所属类型检查时结果自然为false
	 * 
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 * 
	 */
	@Test
	public void Test3() throws InstantiationException, IllegalAccessException, ClassNotFoundException {

		ClassLoader myLoader = new ClassLoader() {
			@Override
			public Class<?> loadClass(String name) throws ClassNotFoundException {
				try {
					String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
					InputStream is = getClass().getResourceAsStream(fileName);
					if (is == null) {
						return super.loadClass(name);
					}
					byte[] bytes = new byte[is.available()];
					is.read(bytes); // 通过自定义类加载器读取class文件的二进制流
					return defineClass(name, bytes, 0, bytes.length);

				} catch (IOException e) {
					e.printStackTrace();
					throw new ClassNotFoundException(name);
				}
			}
		};

		Object obj = myLoader.loadClass("com.technology.jvm.classLoader.NetClassLoaderSimple").newInstance();
		System.out.println(obj.getClass());
		System.out.println(obj instanceof NetClassLoaderSimple);
	}

	/**
	 * 目前常用web服务器中都定义了自己的类加载器，用于加载web应用指定目录下的类库（jar或class），如：Weblogic、Jboss、tomcat等，下面我以Tomcat为例，展示该web容器都定义了哪些个类加载器：
	 *	1、新建一个web工程httpweb
	 *	2、新建一个ClassLoaderServletTest，用于打印web容器中的ClassLoader层次结构
		
	 *	定义自已的类加载器分为两步：
	 *	1、继承java.lang.ClassLoader
	 *	2、重写父类的findClass方法
	 *	读者可能在这里有疑问，父类有那么多方法，为什么偏偏只重写findClass方法？
	 *	 因为JDK已经在loadClass方法中帮我们实现了ClassLoader搜索类的算法，
	 *	 当在loadClass方法中搜索不到类时，loadClass方法就会调用findClass方法来搜索类，
	 *	 所以我们只需重写该方法即可。如没有特殊的要求，一般不建议重写loadClass搜索类的算法。
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public void Test4() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		
        String rootUrl = "http://localhost:8080/lzh-technology/classes";  
        String className = "com.technology.jvm.classLoader.NetClassLoaderSimple";  
        NetworkClassLoader ncl1 = new NetworkClassLoader(rootUrl);  
        Class<?> clazz1 = ncl1.loadClass(className);  
        Object obj1 = clazz1.newInstance();  
	}
	
	/**
	 * tomcate层次的classload 
	 * org.apache.catalina.loader.WebappClassLoader
	 * org.apache.catalina.loader.StandardClassLoader
	 * sun.misc.Launcher$AppClassLoader sun.misc.Launcher$ExtClassLoader null
	 */
	@Test
	public void Test5() {

		try {
			String result = HttpUtils.getUrlAsString("http://localhost:8080/lzh-technology/ClassLoaderServletTest",
					new HashMap<>());
			System.out.println(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
