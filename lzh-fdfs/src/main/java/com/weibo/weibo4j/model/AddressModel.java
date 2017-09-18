package com.weibo.weibo4j.model;

import com.alibaba.fastjson.JSONObject;

public class AddressModel extends WeiboResponse {

	
	private String province; //省
	private String city;   //市
	private String area;   // 区/县
	private String name;   //收货人姓名
	private String mobile;//收货人手机号
	private String address;
	
	public AddressModel(String jsonStr) throws WeiboException {
		
		JSONObject jsonObject=  JSONObject.parseObject(jsonStr);
		JSONObject json= JSONObject.parseObject(jsonObject.get("data").toString());
		province = json.getString("province");
		city = json.getString("city");
		area = json.getString("area");
		name = json.getString("name");
		mobile = json.getString("mobile");
		address = json.getString("address");
	}
	
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "Address [province=" + province + ", city=" + city + ", area=" + area + ", name=" + name + ", mobile="
				+ mobile + ", address=" + address + "]";
	}
	
	
	
}