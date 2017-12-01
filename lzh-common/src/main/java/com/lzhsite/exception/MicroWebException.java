package com.lzhsite.exception;

/**
 * 
 * @author wangzuoxu
 *
 */
public class MicroWebException extends Exception {

	private static final long serialVersionUID = 684725294901056890L;

	private String errorCode;

	private String errorMsg;

	private String args[];

	public MicroWebException() {
		super();
	}

	public MicroWebException(String errorCode, String ... args) {
		this.errorCode = errorCode;
		this.args = args;
	}

	public MicroWebException(String errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public MicroWebException(String errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
		this.errorMsg = message;
	}

	public MicroWebException(String errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
		this.errorMsg = message;
	}

	public MicroWebException(String errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String[] args) {
		this.args = args;
	}
}
