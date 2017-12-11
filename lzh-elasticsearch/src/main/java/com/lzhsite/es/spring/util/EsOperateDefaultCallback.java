package com.lzhsite.es.spring.util;

import org.elasticsearch.action.ActionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的callback函数
 *
 * @author guoqw
 * @since 2017-04-05 11:27
 */
public class EsOperateDefaultCallback implements EsOperateCallback {

    private static Logger logger = LoggerFactory.getLogger(EsOperateDefaultCallback.class);

    public static final EsOperateCallback DEFAULT = new EsOperateDefaultCallback();

    @Override
    public void onSuccess(ActionResponse result) {
        logger.debug("es sdk操作成功，result：{}", result.toString());
    }

    @Override
    public void onFail(Throwable e, EsOperateParam param) {
        logger.error("es sdk操作失败，入参：{}，原因：{}", param, e);
    }
}
