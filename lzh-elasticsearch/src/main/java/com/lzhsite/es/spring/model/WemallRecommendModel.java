package com.lzhsite.es.spring.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by majingqiang on 2017/6/20.
 */
public class WemallRecommendModel implements Serializable{

    /**
	 * 
	 */
	private static final long serialVersionUID = 1125932534065827530L;

	private Long wemallGroupId;

    private Long wemallCouponId;

    /**
     * 0-禁用 1-启用
     */
    private Integer state;

    private Date createTime;

    private Date updateTime;


    public Long getWemallGroupId() {
        return wemallGroupId;
    }

    public void setWemallGroupId(Long wemallGroupId) {
        this.wemallGroupId = wemallGroupId;
    }

    public Long getWemallCouponId() {
        return wemallCouponId;
    }

    public void setWemallCouponId(Long wemallCouponId) {
        this.wemallCouponId = wemallCouponId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
