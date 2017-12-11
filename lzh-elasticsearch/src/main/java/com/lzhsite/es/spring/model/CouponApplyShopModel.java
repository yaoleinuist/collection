package com.lzhsite.es.spring.model;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;

/**
 * 适用门店dto
 */
public class CouponApplyShopModel implements Serializable{

    private static final long serialVersionUID = -3142752595854317572L;

    /**
     * 商户id
     */
    private Long shopId;

    /**
     * 商店名
     */
    private String shopName;

    /**
     * 地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String contact;

    /**
     * 营业时间
     */
    private JSONArray shopHours;

    /**
     * 经度
     */
    private Double lat;

    /**
     * 纬度
     */
    private Double lng;

    /**
     * 距离
     */
    private Double distance;

    /**
     * 是够离我最近
     */
    private boolean nearest;

    
    private String couponInfoCode;
    
    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public JSONArray getShopHours() {
        return shopHours;
    }

    public void setShopHours(JSONArray shopHours) {
        this.shopHours = shopHours;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public boolean isNearest() {
        return nearest;
    }

    public void setNearest(boolean nearest) {
        this.nearest = nearest;
    }

	public String getCouponInfoCode() {
		return couponInfoCode;
	}

	public void setCouponInfoCode(String couponInfoCode) {
		this.couponInfoCode = couponInfoCode;
	}

}
