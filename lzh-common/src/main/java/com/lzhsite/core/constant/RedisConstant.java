package com.lzhsite.core.constant;

/**
 * redis缓存
 * Created by pangpeijie on 16/11/3.
 */
public class RedisConstant {

    /**
     * redis 超时时间 2小时
     */
    public static final int REDIS_SET_TIME_OUT = 7200;

    /**
     * redis 超时时间延长 24小时
     */
    public static final int REDIS_SET_TIME_OUT_LONG = 86400;


    /**
     * redis 超时时间延长 24小时 * 7
     */
    public static final int REDIS_SET_TIME_OUT_WEEK = 604800;

    /**
     * redis 超时时间延长 24小时 * 30
     */
    public static final int REDIS_SET_TIME_OUT_MOUTH = 2592000;

    /**
     * redis 超时时间延长 90天
     */
    public static final int REDIS_SET_TIME_OUT_THREE_MONTHS = 7776000;

    /**
     * 电子券缓存
     */
    public static final String PREFIX = "wemall:";

    /**
     * WEMALL用户token
     */
    public static final String WEMALL_USER_TOKEN = PREFIX + "userToken:%s";

    /**
     * WEMALL券信息
     */
    public static final String COUPON_INFO_WEMALL_PREFIX = PREFIX + "couponInfoWemall:%s";

    /**
     * 渠道信息缓存
     */
    public static final String COUPON_CHANNEL_INFO_PREFIX = PREFIX + "couponChannelId:%d";

    /**
     * 电子券缓存
     */
    public static final String COUPON_INFO_PREFIX = PREFIX + "couponChannelDetailId:%d:applyScene:%d";

    /**
     * 电子券缓存
     */
    public static final String WEMALL_COUPON_INFO_PREFIX = PREFIX + "wemallCouponId:%d";

    /**
     * 电子券详情缓存
     */
    public static final String COUPON_DETAIL_DTO_PREFIX = PREFIX + "couponChannelDetailDTOId:%d";

    /**
     * 商户类型
     */
    public static final String BUSINESS_TYPE_PREFIX = PREFIX + "businessType:%d";

    /**
     * 爱客仕门店缓存
     */
    public static final String SHOP_PREFIX = PREFIX + "xkeshiShop:%d";

    /**
     * 爱客仕集团缓存
     */
    public static final String MERCHANT_PREFIX = PREFIX + "xkeshiMerchant:%d";

    /**
     * 退款原因
     */
    public static final String REFUNDREASON = PREFIX + "refundReason";

    /**
     * 具体的退款原因
     */
    public static final String REFUNDREASONID = PREFIX + "refundReasonId:%s";

    /**
     * 商户类别信息
     */
    public static final String SHOP_CATEGORY_PREFIX = PREFIX + "shopCategory:%d";

    /**
     * 商户简介
     */
    public static final String SHOP_ARTICLE_PREFIX = PREFIX + "shopArticle:%d";

    /**
     * 爱客仕集团商户的独立商户
     */
    public static final String MERCHANT_SHOP_PREFIX = PREFIX + "merchantShop:%d";

    /**
     * redis menu KEY
     */
    public static final String ROLE_MENU_KEY = PREFIX + "role_menu_key:";

    /**
     * 当前登陆账户信息－角色信息 redisKEY
     */
    public static final String ROLE_ALL_KEY = PREFIX + "role_all_key:";

    /**
     * 当前登陆账户父账户-角色 redisKEY
     */
    public static final String ROLE_ALL_PARENT_KEY = PREFIX + "role_all_parent_key:";

    /**
     * redis 用户角色 NAME KEY
     */
    public static final String ROLE_ALL_NAME_KEY = PREFIX + "role_all_name_key:";

    /**
     * redis 用户角色 VALUE KEY
     */
    public static final String ROLE_ALL_VALUE_KEY = PREFIX + "role_all_value_key:";

    /**
     * 当前登陆账户信息－角色账户关联信息 redisKEY
     */
    public static final String ACCOUNT_ROLE_ALL_KEY = PREFIX + "account_roles_all_key:";

    /**
     * 当前登陆账户父账户－角色账户关联信息 redisKEY
     */
    public static final String ACCOUNT_ROLE_ALL_PARENT_KEY = PREFIX + "account_roles_all_parent_key:";

    /**
     * 用户的权限菜单URL
     */
    public static final String MENUS_USER_URL_KEY = PREFIX + "menus_user_url_key:";

    /**
     * 用户信息
     */
    public static final String WEMALL_ACCOUNT = PREFIX + "wemall_account:";

    /**
     * 用户信息
     */
    public static final String WEMALL_PARENT_ACCOUNT = PREFIX + "wemallParentAccount:";

    /**
     * h5登录用户信息KEY
     */
    public static final String WEMALL_ACCOUNT_H5_XUSER = PREFIX + "wemall_account_H5_XUSER:";

    /**
     * 图片缓存
     */
    public static final String PICTURE_LOGO_PREFIX = PREFIX + "piceureId:%d";

    public static final String LOCK_IDNEX_SYNC_PRIEX = PREFIX + "lock:index:sync:saleNum";

    /**
     * 配置缓存前缀
     */
    public static final String CONFIG_LIST_PREFIX = PREFIX + "configs:keyPrefix:%s";

    /**
     * 配置缓存key
     */
    public static final String CONFIG_PREFIX = PREFIX + "config:key:%s";

    /**
     * 支付回调锁
     */
    public static final String PAYSUCCESS_LOCK = PREFIX + "paySuccessLock:orderNo:%s";


    /**
     * 退款锁
     */
    public static final String REFUND_LOCK = PREFIX + "REFUND:refundSerial";

    /**
     * 杭州银行退款锁
     */
    public static final String HZBANK_REFUND_LOCK = PREFIX + "HZBANKREFUND:refundSerial";

    /**
     * 杭州银行退款锁
     */
    public static final String HZBANK_REFUND_QUERY_LOCK = PREFIX + "HZBANKREFUNDQUERY:refundSerial";

    /**
     * C端用户对应的openID
     */
    public static final String C_CLIENT_OPENID = PREFIX + "phone:%s";


    /**
     * C端用户对应的authcode
     */
    public static final String C_CLIENT_WXAUTHCODE = PREFIX + "wxcode:%s";

    /**
     * 卡券中心首页
     */
    public static final String WEMALL_HOME_TEMPLET = PREFIX + "home:templet:%d:%d";

    /**
     * 模板启用状态
     */
    public static final String WEMALL_TEMPLET_STATUS = PREFIX + "templet:status:%d:%d:%d";

    /**
     * 模板跨权访问
     */
    public static final String WEMALL_TEMPLET_CROSS_RIGHT = PREFIX + "cross:right:%d:%d:%d";

    /**
     * 模块区块卡券列表
     */
    public static final String WEMALL_TEMPLET_MODULE_ITEM_COUPON = PREFIX + "templet:module:item:%d";

    /**
     * 序列生成机器id list类型
     */
    public static final String WEMALL_MACHINE_ID = PREFIX + "machine_id";

    /**
     * 序列生成机器id锁
     */
    public static final String WEMALL_MACHINE_ID_LOCK = PREFIX + "machine_id_lock";

    /**
     * 订单流水号
     */
    public static final String WEMALL_PAY_SERIAL = PREFIX + "orderNoSerial:%s";

    /**
     * 订单下单锁
     */
    public static final String COUPON_ORDER_LOCK = PREFIX + "couponOrderLock:%s";

    /**
     * 订单支付方式
     */
    public static final String WEMALL_PAY_TYPE_ORDER_NO = PREFIX + "payType:orderNo:%s";

    /**
     * 提交退款订单
     */
    public static final String REFUND_ORDER = PREFIX + "refund:orderNo:%s:couponDiscountCode:%s";

    /**
     * wemall支付配置缓存
     */
    public static final String WEMALL_PAY_CONFIG = PREFIX + "payConfig:groupId:%s";

    /**
     * wemall分享配置缓存
     */
    public static final String WEMALL_SHARE = PREFIX + "wemallShare:groupId:%s";

    /**
     * 拼团C端详情信息
     */
    public static final String SPELL_GROUP_DETAILS_H5 = PREFIX + "spellGroupDetailsH5:nid:%s";

    /**
     * 拼团C端列表信息
     */
    public static final String SPELL_GROUP_LIST_H5 = PREFIX + "spellGroupListH5:";

    /**
     * 获取wemall个人中心配置订单配置
     */
    public static final String WEMALL_PERSONAL_CENTER = PREFIX + "personalCenter:groupId:%s";

    /**
     * 门店地理位置
     */
    public static final String SHOP_ADDRESS_PREFIX = PREFIX + "xkeshiShopAddress:%d";

    /**
     * RegionInfo
     */
    public static final String SHOP_REGION_PREFIX = PREFIX + "xkeshiRegionInfo:%d";

    /**
     * 通过渠道id查询电子券信息
     */
    public static final String ECOUPON_INFO_PREFIX = PREFIX + "wemallCouponChannelInfo:%d";

    /**
     * 拼团订单支付成功回调锁
     */
    public static final String SPELL_GROUP_ORDER_PAYSUCCESS = PREFIX + "spellGroupOrderPaySuccess:%s";

    /**
     * 拼团订单0元支付锁
     */
    public static final String SPELL_GROUP_ORDER_ZERO_PAYSUCCESS = PREFIX + "spellGroupOrderZeroPaySuccess:%s";

    /**
     * 拼团订单下单锁
     */
    public static final String SPELL_GROUP_ORDER_LOCK = PREFIX + "spellGroupOrderLock:%s";

    /**
     * 电子券上架锁
     */
    public static final String E_COUPON_UP_LOCK = PREFIX + "ECouponUpLock:%s";

    /**
     * 拼团活动
     */
    public static final String SPELL_GROUP_INFO_PREFIX = PREFIX + "spellGroupInfo:%d";

    /**
     * 拼团-订单超时关闭订单
     */
    public static final String SPELL_GROUP_ORDER_OVER_TIME = PREFIX + "spellGroupOrderOverTime";

    /**
     * 拼团-插入拼团失败订单数据
     */
    public static final String SPELL_GROUP_INSERT_FAIL_ORDER = PREFIX + "spellGroupInsetrRefundOrder";

    /**
     * 拼团-提交退款订单
     */
    public static final String SPELL_GROUP_COMMIT_REFUND_ORDER = PREFIX + "spellGroupCommitRefundOrder";


    /**
     * 拼团-杭州银行退款查询
     */
    public static final String SPELL_GROUP_HZBANK_REFUND_ORDER_QUERY = PREFIX + "spellGroupHzbankOrderQuery";

    /**
     * 退款锁
     */
    public static final String SYNC_BUSINESS_INFO_LOCK = PREFIX + "syncBusinessInfo";

    /**
     * 更新拼团状态 1:未开始，2:进行中，3:已结束
     */
    public static final String SPELL_GROUP_STATUS_UPDATE = PREFIX + "spellGroupStatusUpate";

    /**
     * 同步入驻商户锁
     */
    public static final String SYNC_WEMALL_GROUP_ACCOUNT_NAME_LOCK = PREFIX + "WEMALLGROUPACCOUNT:syncWemallGroupAccountName";

    /**
     * 电子券过期下架锁
     */
    public static final String DOWN_EXPIRE_COUPON_LOCK = PREFIX + "lock:expire:down:coupon";

    /**
     * 宋城门票购买用户信息
     */
    public static final String SONG_ECOUPON_ORDER_USER_INFO = PREFIX + "SONG_ECOUPON_ORDER_USER_INFO:orderNo:%s";

    /**
     * 钱包收入锁
     */
    public static final String WALLET_INCOME_LOCK = PREFIX + "walletIncomeLock:%s";

    /**
     * 钱包收入撤销锁
     */
    public static final String WALLET_INCOME_REASON_LOCK = PREFIX + "walletIncomeReasonLock:%s";

    /**
     * 钱包收入锁
     */
    public static final String WALLET_EXPEND_LOCK = PREFIX + "walletExpendLock:%s";

    /**
     * 钱包收入撤销锁
     */
    public static final String WALLET_EXPEND_REASON_LOCK = PREFIX + "walletExpendReasonLock:%s";

    /**
     * 钱包核销锁
     */
    public static final String WALLET_VERIFY_LOCK = PREFIX + "walletVerifyLock:%s";

    /**
     * 行业分类缓存key
     */
    public static final String WEMALL_TRADE_KEY = PREFIX + "trade:%s";

    /**
     * 爱客仕集团商户的独立商户
     */
    public static final String SHOP_SERVICE_PREFIX = PREFIX + "shopService:%d";

    /**
     * WEMALL用户token
     */
    public static final String APP_AUTH_CODE_TOKEN = PREFIX + "appAuthCodeToken:%s";

    /**
     * redis user token前缀
     */
    public static final String REDID_USER_TOKEN_PREFIX = PREFIX + "token:";

    /**
     * 商店街更新状态和取消入驻锁
     */
    public static final String SHOP_STREET_STATUS_LOCK = PREFIX + "shopStreetStatus:%s";

    /**
     * 领券所有wemall
     */
    public static final String GET_COUPON_WEMALL_GROUP_LIST = PREFIX + "getCouponWemallGroupList";

    /**
     * 特定手机号码登录账号密码
     */
    public static final String LOGINMOBILE_MARK_HASH = PREFIX + "loginMobileMarkHash";

    /**
     * 执行支付成功回调dubbo超时key
     */
    public static final String CERTDP = PREFIX + "cert:directoryPathType:%s";

    /**
     * 执行支付成功回调dubbo超时key
     */
    public static final String PROCESS_PAY_CALLBACK = PREFIX + "processPayCallbackTimeOutOrder:orderId:%s";

    /**
     * 订单超时消息幂等
     */
    public static final String PROCESS_CLOST_ORDER_MESSAGE = PREFIX + "processOrderTimeOutMessage:orderId:%s";

    /**
     * 沈一点全部商户信息key
     */
    public static final String SYD_SHOP_LIST = PREFIX + "sydAllShopInfo";

    /**
     * 异常用户订单号集合
     */
    public static final String ISSUE_ORDER_NO_MARK_HASH = PREFIX + "issueOrderNoMarkHash";

    /**
     * 领券设备号集合
     */
    public static final String LING_QUAN_DEVICEID_LOGIN_INFO = PREFIX + "LingquanDeviceIdLoginINFO:%s";
    /**
     * 可用库存（剩余的库存）
     */
    public static final String WEMALL_COUPON_AVAILABLE_STOCK = PREFIX+"couponStock:%s:available" ;

    /**
     * 总库存（上架的库存）
     */
    public static final String WEMALL_COUPON_ALL_STOCK = PREFIX+"couponStock:%s:all";
    /**
     * 锁定库存（下单后支付前的库存）
     */
    public static final String WEMALL_COUPON_LOCK_STOCK = PREFIX+"couponStock:%s:lock";
    /**
     * 完成的库存（已经支付）
     */
    public static final String WEMALL_COUPON_FINISH_STOCK = PREFIX+"couponStock:%s:finish";


    public static final String TEMP_AD_WEMALL_GROUP_ID = PREFIX+"adTempVar:wemallGroupId";

    /**
     * 验证码图片缓存
     */
    public static final String VERIFYCODE = PREFIX + "verifyCode:%s";

    /**
     * 核销码dto信息
     */
    public static final String DISCOUNTCODEDTO = PREFIX + "orderNo:%s";
    /**
     * nid活动某天的领取总次数
     */
    public static final String WEMALL_ACTIVITY_RECEIVE_LOCK = PREFIX+"vipActivity:%s:date:%s:lock";
    /**
     * 活动某天可用领取次数
     */
    public static final String WEMALL_ACTIVITY_RECEIVE_AVAILABLE_STOCK = PREFIX+"vipActivity:%s:date:%s:available";
    /**
     * 活动某天锁定的领取次数
     */
    public static final String WEMALL_ACTIVITY_RECEIVE_ALL_STOCK = PREFIX+"vipActivity:%s:date:%s:all";

    /**
     * 活动某天完成的领取次数
     */
    public static final String WEMALL_AUTO_PUT_DATE = PREFIX+"date";

    public static final String WEMALL_ACTIVITY_RECEIVE_FINISH_STOCK = PREFIX+"vipActivity:%s:date:%s:finish";

    /**
     * 限流规则
     */
    public static final String WEMALL_DISTRUBUTED_LIMITER_RULE = PREFIX + "distributedLimit";

    /**
     * 限流key
     */
    public static final String WEMALL_LIMITER_KEY = PREFIX + "redisLimit:%s:%s";

    /**
     * 限流key次数
     */
    public static final String WEMALL_LIMITER_KEY_COUNT = PREFIX + "redisLimitCounts";

    /**
     * 限流黑名单
     */
    public static final String WEMALL_LIMITER_BLACKLIST_KEY = PREFIX + "redisBlackListLimit";
    
    
}