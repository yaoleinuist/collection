package com.lzhsite.es.spring.model;

/**
 * 描述:
 * 适用门店搜索dto
 *
 * @author pangpeijie
 * @create 2017-11-23 10:20
 */
public class CouponApplyShopSearchModel {

    /**
     * 券编码
     */
    private String couponInfoCode;

    /**
     * 经度
     */
    private Double lat;

    /**
     * 纬度
     */
    private Double lng;

    /**
     * 当前页数
     */
    private Integer currentPage;

    /**
     * 每页分页
     */
    private Integer pageSize;

    public String getCouponInfoCode() {
        return couponInfoCode;
    }

    public void setCouponInfoCode(String couponInfoCode) {
        this.couponInfoCode = couponInfoCode;
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

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
