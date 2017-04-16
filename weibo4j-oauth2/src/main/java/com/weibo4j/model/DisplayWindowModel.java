package com.weibo4j.model;

import com.alibaba.fastjson.JSONObject;

public class DisplayWindowModel extends WeiboResponse {

	
	private String title;  //标题 最大长度60，一个汉字算2",
	private String desc;   //商品简介，最大长度2000，，一个汉字算2（目前橱窗展示图文详情逻辑为：简介+商品图片）",
	private String rprice; //原价(参考价)，两位小数，目前页面不展示，默认同：price",
	private String price;  //售卖价格，页面展示的价格，两位小数",
	private String stock;  //库存，下单流程不在橱窗，目前不做库存判断，橱窗页面也不展示，可以默认为1",
	private String freight;
	private String[] pics;
	private int is_multi_sku; //是否为多规格商品 是1，0否，目前外部接入，没有规格展示场景，可默认0",
	private SkuModel[] skuModel;
	
	
	private int item_id; //商品唯一标示id
	private int item_type; //商品类型，一般情况下用不上，可默认0，在业务方针对不同类型商品有特殊处理需求时使用",
	private int  link_to_weibo;
	
	private String  buy_url;
	
	public DisplayWindowModel(String jsonStr) throws WeiboException {
		
		JSONObject jsonObject=  JSONObject.parseObject(jsonStr);
		JSONObject json= JSONObject.parseObject(jsonObject.get("data").toString());
 
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRprice() {
		return rprice;
	}

	public void setRprice(String rprice) {
		this.rprice = rprice;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getFreight() {
		return freight;
	}

	public void setFreight(String freight) {
		this.freight = freight;
	}

	public String[] getPics() {
		return pics;
	}

	public void setPics(String[] pics) {
		this.pics = pics;
	}

	public int getIs_multi_sku() {
		return is_multi_sku;
	}

	public void setIs_multi_sku(int is_multi_sku) {
		this.is_multi_sku = is_multi_sku;
	}

	public SkuModel[] getSkuModel() {
		return skuModel;
	}

	public void setSkuModel(SkuModel[] skuModel) {
		this.skuModel = skuModel;
	}

	public int getItem_id() {
		return item_id;
	}

	public void setItem_id(int item_id) {
		this.item_id = item_id;
	}

	public int getItem_type() {
		return item_type;
	}

	public void setItem_type(int item_type) {
		this.item_type = item_type;
	}

	public int getLink_to_weibo() {
		return link_to_weibo;
	}

	public void setLink_to_weibo(int link_to_weibo) {
		this.link_to_weibo = link_to_weibo;
	}

	public String getBuy_url() {
		return buy_url;
	}

	public void setBuy_url(String buy_url) {
		this.buy_url = buy_url;
	}
	
 
}
