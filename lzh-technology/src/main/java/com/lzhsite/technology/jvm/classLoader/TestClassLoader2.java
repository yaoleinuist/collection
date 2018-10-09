package com.lzhsite.technology.jvm.classLoader;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.junit.Test;

import com.lzhsite.core.utils.JSONUtil;

public class TestClassLoader2 {

	// 1.自己定义URLClassLoader对象加载外部jar包，针对jar包里面不再出现别的jar包的情况，即只解析.class文件
	@Test
	public void test1() {

		String path = "D://demosrc//lzh-maven-parent//lzh-technology//src//main//webapp//WEB-INF//lib//html2image-0.9.jar";// 外部jar包的路径
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();// 所有的Class对象
		Map<Class<?>, Annotation[]> classAnnotationMap = new HashMap<Class<?>, Annotation[]>();// 每个Class对象上的注释对象
		Map<Class<?>, Map<Method, Annotation[]>> classMethodAnnoMap = new HashMap<Class<?>, Map<Method, Annotation[]>>();// 每个Class对象中每个方法上的注释对象
		try {
			JarFile jarFile = new JarFile(new File(path));
			URL url = new URL("file:" + path);
			ClassLoader loader = new URLClassLoader(new URL[] { url });// 自己定义的classLoader类，把外部路径也加到load路径里，使系统去该路经load对象
			Enumeration<JarEntry> es = jarFile.entries();
			while (es.hasMoreElements()) {
				JarEntry jarEntry = (JarEntry) es.nextElement();
				String name = jarEntry.getName();
				if (name != null && name.endsWith(".class")) {// 只解析了.class文件，没有解析里面的jar包
					// 默认去系统已经定义的路径查找对象，针对外部jar包不能用
					// Class<?> c =
					// Thread.currentThread().getContextClassLoader().loadClass(name.replace("/",
					// ".").substring(0,name.length() - 6));
					Class<?> c = loader.loadClass(name.replace("/", ".").substring(0, name.length() - 6));// 自己定义的loader路径可以找到
					System.out.println(c);
					classes.add(c);
					Annotation[] classAnnos = c.getDeclaredAnnotations();
					classAnnotationMap.put(c, classAnnos);
					Method[] classMethods = c.getDeclaredMethods();
					Map<Method, Annotation[]> methodAnnoMap = new HashMap<Method, Annotation[]>();
					for (int i = 0; i < classMethods.length; i++) {
						Annotation[] a = classMethods[i].getDeclaredAnnotations();
						methodAnnoMap.put(classMethods[i], a);
					}
					classMethodAnnoMap.put(c, methodAnnoMap);
				}
			}
			System.out.println(classes.size());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 2.第二种情况是针对加载jar包里面的jar包的Class对象，还有读取某一个properties文件的方法。
	@Test
	public void test2() {
		String path = "E:/.xkeshi/repository/com/lzhsite/component/lzh-fdfs/1.0-SNAPSHOT/lzh-fdfs-1.0-SNAPSHOT-sources.jar";// 此jar包里还有别的jar包
		try {
			JarFile jarfile = new JarFile(new File(path));
			Enumeration<JarEntry> es = jarfile.entries();
			while (es.hasMoreElements()) {
				JarEntry je = es.nextElement();
				String name = je.getName();
				if (name.endsWith(".jar")) {// 读取jar包里的jar包
					File f = new File(name);
					JarFile j = new JarFile(f);
					Enumeration<JarEntry> e = j.entries();
					while (e.hasMoreElements()) {
						JarEntry jarEntry = (JarEntry) e.nextElement();
						System.out.println(jarEntry.getName());
						// .........接下去和上面的方法类似
					}
				}
				// System.out.println(je.getName());
				if (je.getName().equals("log4j.properties")) {
					InputStream inputStream = jarfile.getInputStream(je);
					Properties properties = new Properties();
					properties.load(inputStream);
					Iterator<Object> ite = properties.keySet().iterator();
					while (ite.hasNext()) {
						Object key = ite.next();
						System.out.println(key + " : " + properties.get(key));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 3.第三种情况是在该项目下获取某个包的Class对象，当然了，测试方法是在该项目下写的
	// （这样classLoader就直接可以知道对象了，不需要再自定义URLClassLoader了，
	// 用Thread.currentThread().getContextClassLoader().loadClass(.....)
	// 就可以直接获得Class对象了，回去ClassPath下找，
	// System.out.print(System.getProperty("java.class.path"))就可以找到classPath路径）
	@Test
	public void test3() {
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		boolean flag = true;// 是否循环迭代

		String packName = "com.lzhsite.technology.jvm";
		String packDir = packName.replace(".", "/");
		Enumeration<URL> dir;
		try {
			dir = Thread.currentThread().getContextClassLoader().getResources(packDir);
			while (dir.hasMoreElements()) {
				URL url = dir.nextElement();
				System.out.println("url:***" + url);
				String protocol = url.getProtocol();// 获得协议号
				if ("file".equals(protocol)) {
					System.err.println("file类型的扫描");
					String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
					System.out.println("filePath :" + filePath);
					findAndAddClassesInPackageByFile(packName, filePath, flag, classes);
				} else if ("jar".equals(protocol)) {
					System.err.println("jar类型扫描");
					JarFile jar;
					jar = ((JarURLConnection) url.openConnection()).getJarFile();
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						System.out.println(">>>>:" + name);
						// ......
					}

				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(classes.size());
		System.out.println(JSONUtil.toJson(classes));

	}

	private static void findAndAddClassesInPackageByFile(String packName, String filePath, final boolean flag,
			Set<Class<?>> classes) {
		File dir = new File(filePath);
		if (!dir.exists() || !dir.isDirectory()) {
			System.out.println("此路径下没有文件");
			return;
		}
		File[] dirfiles = dir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return flag && pathname.isDirectory() || pathname.getName().endsWith(".class");
			}
		});
		for (File file : dirfiles) {
			if (file.isDirectory()) {// 如果是目录，继续扫描
				findAndAddClassesInPackageByFile(packName + "." + file.getName(), file.getAbsolutePath(), flag,
						classes);
			} else {// 如果是文件
				String className = file.getName().substring(0, file.getName().length() - 6);
				System.out.println("类名：" + className);
				try {
					classes.add(Thread.currentThread().getContextClassLoader().loadClass(packName + "." + className));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
