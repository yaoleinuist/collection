/**
 * 
 */
package com.lzhsite.technology.freemark;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * freemarker测试
 * 
 * @author hongten(hongtenzone@foxmail.com)<br>
 * @date 2013-4-5
 */
public class MyFreeMarker {

	private static Configuration configuration;
	private static Template template;
	private static Writer writer;
	/**
	 * 模板文件的存放路径，这里是存放在项目根目录下的ftls文件夹中
	 */
	public static final String FTLS_PATH = "/ftls";

	public static final String MESSAGE = "message";
	public static final String HELLO_WORLD = "Hello World!";
	public static final String HONGTEN_HELLO_WORLD_FTL = "hongten-helloworld.ftl";
	public static final String HONGTEN_MY_JAVA_FILE_FTL = "hongten-myJavaFile.ftl";

	// bean
	public static final String BEAN = "bean";
	public static final String BEAN_URL = "com.bean";

	// annotation
	public static final String ANNOTATION = "annotation";
	public static final String ANNOTATION_AUTHOR_NAME = "hongten";
	public static final String ANNOTATION_AUTHOR_MAIL = "hongtenzone@foxmail.com";
	public static final String ANNOTATION_VERSION = "1.0";

	// date formate
	public static final String DATE_FROMATE = "yyyy-MM-dd";

	public static void main(String[] args) throws Exception {
		// helloWorld(FTLS_PATH, HONGTEN_HELLO_WORLD_FTL);
		myJavaFile(FTLS_PATH, BEAN_URL, HONGTEN_MY_JAVA_FILE_FTL);
	}

	/**
	 * 利用模板在控制台打印helloworld信息
	 * 
	 * @param path
	 *            模板存放的路径
	 * @param ftlFile
	 *            模板文件
	 * @throws Exception
	 */
	public static void helloWorld(String path, String ftlFile) throws Exception {
		// 创建Freemarker配置实例
		configuration = new Configuration();
		configuration.setDirectoryForTemplateLoading(new File(path));

		// 创建数据模型
		Map<String, String> root = new HashMap<String, String>();
		root.put(MESSAGE, HELLO_WORLD);

		// 加载模板文件
		template = configuration.getTemplate(ftlFile);

		// 显示生成的数据，这里打印在控制台
		writer = new OutputStreamWriter(System.out);
		template.process(root, writer);
		writer.flush();
		writer.close();
	}

	/**
	 * 利用freemarker生成自定义的javaBean
	 * 
	 * @param path
	 *            模板路径
	 * @param packageUrl
	 *            javaBean的url，即package名称
	 * @param ftlFile
	 *            使用的模板文件
	 * @throws Exception
	 */
	public static void myJavaFile(String path, String packageUrl, String ftlFile) throws Exception {
		// 创建Freemarker配置实例
		configuration = new Configuration();
		configuration.setDirectoryForTemplateLoading(new File(System.getProperty("user.dir") + "/src/main/java" +path+"/"));

		// 创建数据模型
		Map<String, Object> root = new HashMap<String, Object>();
		Bean bean = new Bean();
		bean.setName("User");
		bean.setLowerName("user");
		bean.setBeanUrl(packageUrl);
		root.put(BEAN, bean);

		Annotation annotation = new Annotation();
		annotation.setAuthorMail(ANNOTATION_AUTHOR_MAIL);
		annotation.setAuthorName(ANNOTATION_AUTHOR_NAME);
		annotation.setVersion(ANNOTATION_VERSION);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FROMATE);
		annotation.setDate(simpleDateFormat.format(new Date()));
		root.put(ANNOTATION, annotation);

		// 加载模板文件
		template = configuration.getTemplate(ftlFile);

		String beanPath = System.getProperty("user.dir") + "/src/main/java/" + packageUrl.replace(".", "/") + "/";
		File filePath = new File(beanPath);
		if (!filePath.exists()) {
			filePath.mkdirs();
		}

		String filePathOfBean =  beanPath + "/User.java";
		File file = new File(filePathOfBean);
		if (!file.exists()) {
			file.createNewFile();
		}

		// 显示生成的数据
		writer = new FileWriter(file);
		template.process(root, writer);
		writer.flush();
		writer.close();
	}
}
