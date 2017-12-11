package com.lzhsite.es.spring.cilent;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.delete.DeleteAction;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.lzhsite.db.Pager;
import com.lzhsite.es.spring.model.CouponApplyShopModel;
import com.lzhsite.es.spring.model.CouponApplyShopSearchModel;
import com.lzhsite.es.spring.util.EsOperateSdk;

/**
 * 描述:
 * 券适用商户ES操作
 *
 * @author pangpeijie
 * @create 2017-11-22 13:49
 */
@Component
public class EsApplyShopClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(EsApplyShopClient.class);

    private String indexName="es.coupon.apply.shop";

    private String typeName = "couponApplyShop";

    @Autowired
    private EsOperateSdk esOperateSdk;

    /**
     * 保存商户信息到es
     */
    public void batchSave(List<CouponApplyShopModel> couponApplyShopDtos) {
        if(null == couponApplyShopDtos || couponApplyShopDtos.size() == 0){
            return;
        }
        TransportClient client = esOperateSdk.esClient();
        if(null != couponApplyShopDtos && couponApplyShopDtos.size() > 0){
            LOGGER.info("batchSave 批量保存,券编码:{} 新增列表大小:{}",couponApplyShopDtos.get(0).getCouponInfoCode(),couponApplyShopDtos.size());
            try{
                BulkRequestBuilder bulkRequest = client.prepareBulk();
                for (CouponApplyShopModel couponApplyShopDto:couponApplyShopDtos){
                    bulkRequest.add(client.prepareIndex(indexName, typeName)
                            .setSource(JSON.toJSONString(couponApplyShopDto)));
                }
                bulkRequest.execute().actionGet();
            }catch (Exception ex){
                LOGGER.error("批量新增操作失败，券编码:{}",couponApplyShopDtos.get(0).getCouponInfoCode(),ex);
            }
        }
    }

    /**
     * 通过券编码批量删除
     * @param couponInfoCode
     */
    public void batchDeleteByCouponInfoCode(String couponInfoCode){
        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(esOperateSdk.esClient(),SearchAction.INSTANCE)
                .setIndices(indexName)
                .setTypes(typeName)
                .setSize(Integer.MAX_VALUE)
                .setQuery(QueryBuilders.termsQuery("couponInfoCode", couponInfoCode));
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        TransportClient client = esOperateSdk.esClient();
        BulkRequestBuilder deleteBuilder = client.prepareBulk();
        SearchHits searchHits = searchResponse.getHits();
        if(searchHits.getHits().length > 0){
            for (SearchHit hit : searchHits.getHits()) {
                deleteBuilder.add(new DeleteRequestBuilder(client, DeleteAction.INSTANCE,indexName).setType(typeName).setId(hit.getId()));
            }
            deleteBuilder.execute().actionGet();
            LOGGER.info("券使用商户删除,券编码：{} 删除数量：{}",couponInfoCode,searchHits.getHits().length);
        }
    }

    /**
     * 按照经纬度进行排序
     * @param couponApplyShopSearchDto
     * @return
     */
    public Pager<CouponApplyShopModel> search(CouponApplyShopSearchModel couponApplyShopSearchDto){
        int from = (couponApplyShopSearchDto.getCurrentPage() - 1) * couponApplyShopSearchDto.getPageSize();
        SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(esOperateSdk.esClient(),SearchAction.INSTANCE)
                .setIndices(indexName)
                .setTypes(typeName)
                .setFrom(from)
                .setSize(couponApplyShopSearchDto.getPageSize());
        BoolQueryBuilder  boolQueryBuilder = QueryBuilders.boolQuery();
        if (couponApplyShopSearchDto.getCouponInfoCode() != null) {
        	boolQueryBuilder.must(QueryBuilders.commonTermsQuery("couponInfoCode", couponApplyShopSearchDto.getCouponInfoCode()));
        }
        searchRequestBuilder.setPostFilter(boolQueryBuilder);
        if(null != couponApplyShopSearchDto.getLat() && null != couponApplyShopSearchDto.getLng() ){
            searchRequestBuilder.addSort(SortBuilders.geoDistanceSort("location")
                    .point(couponApplyShopSearchDto.getLat(), couponApplyShopSearchDto.getLng())
                    .unit(DistanceUnit.METERS)
                    .order(SortOrder.ASC));
        }
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();
        SearchHits searchHits = searchResponse.getHits();
        List<CouponApplyShopModel> list = new ArrayList<>();
        for (SearchHit hit : searchHits.getHits()) {
            try {
                CouponApplyShopModel couponApplyShopDto = JSON.parseObject(hit.getSourceAsString(), CouponApplyShopModel.class);
                try {
                    //有距离排序的时候，才计算距离值
                    if(null != couponApplyShopSearchDto.getLat() && null != couponApplyShopSearchDto.getLng() ){
                        // 获取距离值，并保留两位小数点
                        BigDecimal geoDis = new BigDecimal((Double) hit.getSortValues()[0]);
                        couponApplyShopDto.setDistance(geoDis.setScale(2, BigDecimal.ROUND_HALF_DOWN).doubleValue());
                    }
                }catch (Exception ex){}

                list.add(couponApplyShopDto);
            } catch (Exception ex) {
                LOGGER.error("CouponApplyShopDto转换出错,信息为:{}", hit.getSourceAsString(), ex);
            }
        }
        Pager pager = new Pager();
        pager.setCurrentPage(couponApplyShopSearchDto.getCurrentPage());
        pager.setPageCount(couponApplyShopSearchDto.getPageSize());
        pager.setList(list);
        pager.setTotalCount((int) searchHits.getTotalHits());
        return pager;
    }

}
