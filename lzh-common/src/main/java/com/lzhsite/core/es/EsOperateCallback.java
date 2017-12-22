package com.lzhsite.core.es;

import org.elasticsearch.action.ActionResponse;

/**
 * Es操作回调函数
 *
 * @author guoqw
 * @since 2017-04-05 11:22
 */
public interface EsOperateCallback {

    /**
     * 成功是执行的操作
     *
     * @param result 返回结果
     */
    void onSuccess(ActionResponse result);

    /**
     * 失败时执行的操作
     *
     * @param e     异常
     * @param param 入参信息
     */
    void onFail(Throwable e, EsOperateParam param);
}
