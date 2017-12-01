package com.lzhsite.pojo;

public class LogsBean{
	/**
	 * 用户真实ip
	 */
	private String ip;
	/**
	 * 商户类型
	 */
	private String busiType;
	/**
	 * 商户编号
	 */
	private String busiId;
	/**
	 * 毫秒数
	 */
	private String timestamp;
	/**
	 * 浏览器信息
	 */
	private String userAgent;
	
	private String uri;
	
	/**
	 * 会员编号
	 */
	private String memberId;
	/**
	 * 域名
	 */
	private String serverName;

	/**
	 * x卡用户Id
	 */
	private String XcardId;
	
	
	private String url;

	public String getXcardId() {
		return XcardId;
	}

	public void setXcardId(String xcardId) {
		XcardId = xcardId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getBusiId() {
		return busiId;
	}

	public void setBusiId(String busiId) {
		this.busiId = busiId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
