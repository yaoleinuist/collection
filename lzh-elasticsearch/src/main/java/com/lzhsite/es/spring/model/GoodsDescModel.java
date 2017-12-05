package com.lzhsite.es.spring.model;

import java.util.List;

//热搜词对象
public class GoodsDescModel {

	private int uuid;
	private String goodsTitle;
	// 商品的拼音【全拼，首字母】
	private String goodsSpilt;
	private List<String> goodsDesc;
	// 商品分类
	// ...
	public int getUuid() {
		return uuid;
	}
	public void setUuid(int uuid) {
		this.uuid = uuid;
	}
	public String getGoodsTitle() {
		return goodsTitle;
	}
	public void setGoodsTitle(String goodsTitle) {
		this.goodsTitle = goodsTitle;
	}
	public String getGoodsSpilt() {
		return goodsSpilt;
	}
	public void setGoodsSpilt(String goodsSpilt) {
		this.goodsSpilt = goodsSpilt;
	}
	public List<String> getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(List<String> goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	@Override
	public String toString() {
		return "GoodsModel [uuid=" + uuid + ", goodsTitle=" + goodsTitle + ", goodsSpilt=" + goodsSpilt + ", goodsDesc="
				+ goodsDesc + "]";
	}
	
}
