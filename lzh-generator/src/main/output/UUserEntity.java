package com.lzhsite.entities.wemall;

import java.io.Serializable;
import java.util.Date;



/**
 * 
 * @date 2017-12-01 18:39:16
 */
public class UUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//
	private Long id;
	//用户昵称
	private String nickname;
	//邮箱|登录帐号
	private String email;
	//密码
	private String pswd;
	//创建时间
	private Date createTime;
	//最后登录时间
	private Date lastLoginTime;
	//1:有效，0:禁止登录
	private Long status;


	/**
	 * 设置：
	 */
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	/**
	 * 设置：用户昵称
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getNickname() {
		return nickname;
	}

	/**
	 * 设置：邮箱|登录帐号
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	/**
	 * 设置：密码
	 */
	public void setPswd(String pswd) {
		this.pswd = pswd;
	}

	public String getPswd() {
		return pswd;
	}

	/**
	 * 设置：创建时间
	 */
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	/**
	 * 设置：最后登录时间
	 */
	public void setLastLoginTime(Date lastLoginTime) {
		this.lastLoginTime = lastLoginTime;
	}

	public Date getLastLoginTime() {
		return lastLoginTime;
	}

	/**
	 * 设置：1:有效，0:禁止登录
	 */
	public void setStatus(Long status) {
		this.status = status;
	}

	public Long getStatus() {
		return status;
	}
}
