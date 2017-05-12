package com.technology.test;

import java.io.FileReader;
import java.net.URL;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class TestJs {

	public static void main(String[] args) throws Exception {

		URL classPath = Thread.currentThread().getContextClassLoader().getResource("");
		String proFilePath = classPath.toString();

		// 移除开通的file:/六个字符
		proFilePath = proFilePath.substring(6);

		// 如果为window系统下,则把路径中的路径分隔符替换为window系统的文件路径分隔符
		proFilePath = proFilePath.replace("/", java.io.File.separator);

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");
		String jsFileName = proFilePath + "..\\lzh-technology\\static\\test.js";
		// 读取js文件
		FileReader reader = new FileReader(jsFileName);
		// 执行指定脚本
		engine.eval(reader);
		if (engine instanceof Invocable) {
			Invocable invoke = (Invocable) engine;
			// 调用merge方法，并传入两个参数
			// c = merge(2, 3);
			Double c = (Double) invoke.invokeFunction("add", 2, 3);
			System.out.println("c = " + c);
		}
		reader.close();
	}
}
