package com.lzhsite.es.spring.cilent;

import java.util.Date;

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.lzhsite.es.spring.model.WemallRecommendModel;
import com.lzhsite.es.spring.util.EsOperateCallback;
import com.lzhsite.es.spring.util.EsOperateParam;
import com.lzhsite.es.spring.util.EsOperateSdk;
import com.lzhsite.es.spring.util.EsOperateTemplate;
 
@Component
public class EsWemallRecommendClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsWemallRecommendClient.class);

    @Value("${es.recommend.indexName}")
    private String indexName;

    private String typeName = "wemallRecommend";

    @Autowired
    private EsOperateSdk esOperateSdk;

    /**
     * 保存wemall首页推荐电子券
     */
    public void save(WemallRecommendModel WemallRecommendModel) {
        esOperateSdk.templateWithCallback(transportClient -> esOperateSdk.esClient().prepareIndex(
                indexName, typeName, WemallRecommendModel.getWemallGroupId().toString())
                .setSource(JSON.toJSONString(WemallRecommendModel)).execute(), new EsOperateCallback() {
            @Override
            public void onSuccess(ActionResponse result) {
                LOGGER.info("保存成功wemall首页推荐电子券WemallRecommendModel:{}", WemallRecommendModel);
            }

            @Override
            public void onFail(Throwable e, EsOperateParam param) {
                LOGGER.info("保存失败wemall首页推荐电子券WemallRecommendModel:{}", WemallRecommendModel, e);
            }
        });
    }

    /**
     * 通过wemallGroupId查看首页推荐电子券
     */
    public WemallRecommendModel get(Long wemallGroupId) {
        return esOperateSdk.queryById(EsOperateParam.queryBuilder()
                .indexName(indexName)
                .typeName(typeName)
                .id(wemallGroupId)
                .build(), WemallRecommendModel.class);
    }

    /**
     * 更新wemallGroupId修改启用状态
     *
     * @param wemallGroupId
     * @param key
     * @param value
     * @return
     */
    public void updateState(Long wemallGroupId, Object key, Object value) {
        esOperateSdk.templateWithCallback(new EsOperateTemplate<UpdateResponse>() {
            @Override
            public ListenableActionFuture<UpdateResponse> template(TransportClient transportClient) {
                return esOperateSdk.esClient().prepareUpdate(indexName, typeName, wemallGroupId.toString()).setDoc(key, value, "updateTime", new Date()).execute();
            }
        }, new EsOperateCallback() {
            @Override
            public void onSuccess(ActionResponse actionResponse) {

            }

            @Override
            public void onFail(Throwable throwable, EsOperateParam esOperateParam) {
                LOGGER.info("通过wemallGroupId修改启用状态失败wemallGroupId:{} key：{} value:{}", wemallGroupId, key, value);
            }
        });
    }
}
