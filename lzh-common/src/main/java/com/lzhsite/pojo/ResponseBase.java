package com.lzhsite.pojo;

public class ResponseBase {
	
	/**
	 * 返回状态
	 */
	private String state;
	
	/**
	 * 返回数据
	 */
	private Object data;

	/**
	 * 错误信息
	 */
	private ErrorsBean error;

	public ErrorsBean getError() {
		return error;
	}

	public void setError(ErrorsBean error) {
		this.error = error;
	}

	public ResponseBase() {
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "ResponseBase [state=" + state + ", data=" + data + "]";
	}


}
