package com.lzhsite.es.util;


import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.client.transport.TransportClient;

/**
 * Es操作模板函数
 *
 * @author guoqw
 * @since 2017-04-05 15:30
 */
public interface EsOperateTemplate<Response extends ActionResponse> {

    ListenableActionFuture<Response> template(TransportClient client);
}