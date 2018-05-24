package com.lzhsite.technology.nio.demo3.handler;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.lzhsite.technology.nio.demo3.Request;
import com.lzhsite.technology.nio.demo3.Response;
import com.lzhsite.technology.nio.demo3.event.EventAdapter;

public class TimeHandler extends EventAdapter {
	public TimeHandler() {
	}

	public void onWrite(Request request, Response response) throws Exception {
		String command = new String(request.getDataInput());
		String time = null;
		Date date = new Date();

		// 判断查询命令
		//if (command.equals("GB")) {
		if (command.startsWith("GB")) {
			// 中文格式
			DateFormat cnDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.CHINA);
			time = cnDate.format(date);
		} else {
			// 英文格式
			DateFormat enDate = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, Locale.US);
			time = enDate.format(date);
		}

		response.send((command + "    " + time).getBytes());
	}
}
