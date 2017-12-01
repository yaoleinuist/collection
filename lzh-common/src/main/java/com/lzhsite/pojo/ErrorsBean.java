package com.lzhsite.pojo;

import org.apache.commons.lang3.StringUtils;

public class ErrorsBean {
	
	/**
	 * 错误代码
	 */
	private String errorCode;
	/**
	 * 错误信息
	 */
	private String msg;

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
		if(StringUtils.isBlank(this.getErrorCode())){
			this.errorCode = "MICROWEB_COMMOM_UNKNOW";
		}
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;


	}


}
