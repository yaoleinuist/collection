package com.lzhsite.es.spring.publics.search;

 

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import com.lzhsite.exception.MicroWebException;
import com.lzhsite.util.JsonUtil;

/**
 * 庙街数据埋点数据结构
 */
public class IMJMicroPoint implements Serializable{
    /** 新添埋点**/
    @JSONField(name = "bhv_type")
    private Long bhvType;
    @JSONField(name = "merchant_id")
    private Long merchantId;
    @JSONField(name = "merchant_name")
    private String merchantName;
    @JSONField(name = "action_type_code")
    private Long actionTypeCode;
    @JSONField(name = "stayed_time")
    private Long stayedTime;
    @JSONField(name = "action_type_name")
    private String actionTypeName;
    @JSONField(name = "busi_type")
    private String busiType;
    @JSONField(name = "access_model_code")
    private Long accessModelCode;
    @JSONField(name = "access_model_name")
    private String accessModelName;

    /** 以下均是原微商城埋点数据**/
    @JSONField(name = "shop_id")
    private Long shopId;
    @JSONField(name = "shop_name")
    private String shopName;
    @JSONField(name = "shop_full_name")
    private String shopFullName;
    @JSONField(serialize = false)
    private String shopAddress;
    @JSONField(serialize = false)
    private String shopPhone;
    @JSONField(name = "open_micro_time")
    private String openMicroTime;
    /** 兼容前端*/
    private Date openMicroTimeDate;
    @JSONField(name = "product_id")
    private Long productId;
    @JSONField(name = "product_name")
    private String productName;
    @JSONField(name = "product_price")
    private String productPrice;
    @JSONField(serialize = false)
    private String productDesc;
    @JSONField(name = "item_category_id")
    private Long itemCategoryId;
    @JSONField(name = "item_category_name")
    private String itemCategoryName;
    @JSONField(name = "scope_type")
    private int scopeType;
    @JSONField(name = "session_id")
    private String sessionId;
    @JSONField(name = "user_id")
    private String userId;
    @JSONField(name = "user_name")
    private String userName;
    @JSONField(serialize = false)
    private int userType;
    @JSONField(name = "login_status")
    private int loginStatus;
    @JSONField(name = "user_phone")
    private String userPhone;
    @JSONField(name = "create_time")
    private String createTime;

    @JSONField(name = "create_dt")
    private String createDt;

    /** 电子券*/
    @JSONField(name = "coupon_info_code")
    private String couponInfoCode;
    @JSONField(name = "coupon_info_name")
    private String couponInfoName;
    @JSONField(name = "coupon_id")
    private Long couponId;

    public Long getBhvType() {
        return bhvType;
    }

    public void setBhvType(Long bhvType) {
        this.bhvType = bhvType;
    }

    public Date getOpenMicroTimeDate() {
        return openMicroTimeDate;
    }

    public void setOpenMicroTimeDate(Date openMicroTimeDate) {
        this.openMicroTimeDate = openMicroTimeDate;
    }

    public String getCouponInfoCode() {
        return couponInfoCode;
    }

    public void setCouponInfoCode(String couponInfoCode) {
        this.couponInfoCode = couponInfoCode;
    }

    public String getCouponInfoName() {
        return couponInfoName;
    }

    public void setCouponInfoName(String couponInfoName) {
        this.couponInfoName = couponInfoName;
    }

    public Long getCouponId() {
        return couponId;
    }

    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopFullName() {
        return shopFullName;
    }

    public void setShopFullName(String shopFullName) {
        this.shopFullName = shopFullName;
    }

    public String getShopAddress() {
        return shopAddress;
    }

    public void setShopAddress(String shopAddress) {
        this.shopAddress = shopAddress;
    }

    public String getShopPhone() {
        return shopPhone;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public String getOpenMicroTime() {
        return openMicroTime;
    }

    public void setOpenMicroTime(String openMicroTime) {
        this.openMicroTime = openMicroTime;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public Long getItemCategoryId() {
        return itemCategoryId;
    }

    public void setItemCategoryId(Long itemCategoryId) {
        this.itemCategoryId = itemCategoryId;
    }

    public String getItemCategoryName() {
        return itemCategoryName;
    }

    public void setItemCategoryName(String itemCategoryName) {
        this.itemCategoryName = itemCategoryName;
    }

    public int getScopeType() {
        return scopeType;
    }

    public void setScopeType(int scopeType) {
        this.scopeType = scopeType;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getLoginStatus() {
        return loginStatus;
    }

    public void setLoginStatus(int loginStatus) {
        this.loginStatus = loginStatus;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateDt() {
        return createDt;
    }

    public void setCreateDt(String createDt) {
        this.createDt = createDt;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public Long getActionTypeCode() {
        return actionTypeCode;
    }

    public void setActionTypeCode(Long actionTypeCode) {
        this.actionTypeCode = actionTypeCode;
    }

    public String getActionTypeName() {
        return actionTypeName;
    }

    public void setActionTypeName(String  actionTypeName) {
        this.actionTypeName = actionTypeName;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public Long getAccessModelCode() {
        return accessModelCode;
    }

    public void setAccessModelCode(Long accessModelCode) {
        this.accessModelCode = accessModelCode;
    }

    public String getAccessModelName() {
        if(accessModelCode.equals(1l)){
            accessModelName = "微店铺";
        }else if(accessModelCode.equals(2l)){
            accessModelName = "微外卖";
        }
        return accessModelName;
    }

    public void setAccessModelName(String accessModelName) {
        this.accessModelName = accessModelName;
    }

    public Long getStayedTime() {
        return stayedTime;
    }

    public void setStayedTime(Long stayedTime) {
        this.stayedTime = stayedTime;
    }

 
    public static void main(String[] args) {
    	IMJMicroPoint imjMicroPoint = new IMJMicroPoint();
    	imjMicroPoint.setBhvType(123L);
    	imjMicroPoint.setMerchantId(123123L); 
    	imjMicroPoint.setMerchantName("merchantName"); 
    	imjMicroPoint.setActionTypeCode(32312L);
    	imjMicroPoint.setStayedTime(18020190012L);
    	imjMicroPoint.setActionTypeName("actionTypeName");
    	imjMicroPoint.setBusiType("busiType");
    	imjMicroPoint.setAccessModelCode(432L);
    	imjMicroPoint.setAccessModelName("accessModelName");
    	imjMicroPoint.setShopId(12312L);
    	imjMicroPoint.setShopName("shopName");
    	imjMicroPoint.setShopFullName("shopFullName");
    	imjMicroPoint.setShopAddress("shopAddress");
    	imjMicroPoint.setShopPhone("shopPhone");
    	imjMicroPoint.setOpenMicroTime("1231232131233");
    	imjMicroPoint.setOpenMicroTimeDate(new Date());
    	imjMicroPoint.setProductId(54332L);
    	imjMicroPoint.setProductName("productName");
    	imjMicroPoint.setProductPrice("12312");
    	imjMicroPoint.setProductDesc("productDesc");
    	imjMicroPoint.setItemCategoryId(34323L);
    	imjMicroPoint.setItemCategoryName("itemCategoryName");
    	imjMicroPoint.setScopeType(1);
    	imjMicroPoint.setSessionId("34323232");
    	imjMicroPoint.setUserId("2434");
    	imjMicroPoint.setUserName("userName");
    	imjMicroPoint.setUserType(1);
    	imjMicroPoint.setLoginStatus(1);
    	imjMicroPoint.setUserPhone("18090230202");
    	imjMicroPoint.setCreateTime("2017-01-02 12:32:21");
    	imjMicroPoint.setCreateDt("2017-01-02 12:32:21");
    	imjMicroPoint.setCouponInfoCode("123123");
    	imjMicroPoint.setCouponInfoName("couponInfoName");
    	imjMicroPoint.setCouponId(893729L);
    	try {
			System.out.println(JsonUtil.obj2Json(imjMicroPoint));
		} catch (MicroWebException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
}
