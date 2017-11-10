package com.lzhsite.es.demo;


import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;

import com.alibaba.fastjson.JSONObject;
import com.lzhsite.es.spring.crud.UserModel;

public class DSLDemo {

	private static final String INDEX_NAME="ibeifeng-java";
	private static final String TYPE_NAME="javaapi";
	
	// DSL查询
	public static List<UserModel> getByCondition(String name){
		List<UserModel> users = new ArrayList<UserModel>();
		// 基本的DSL查询
//		SearchRequestBuilder builder = ESHelper.getESClient()
//				.prepareSearch(INDEX_NAME)
//		        .setTypes(TYPE_NAME)
//		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//		        .setQuery(QueryBuilders.termQuery("name", name));
		
		// 带分页的查询
//		SearchRequestBuilder builder = ESHelper.getESClient()
//				.prepareSearch(INDEX_NAME)
//		        .setTypes(TYPE_NAME)
//		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//		        .setQuery(QueryBuilders.prefixQuery("name", name))
//		        .setFrom(1)
//		        .setSize(2);
		
		// 带排序的查询
		SearchRequestBuilder builder = ESHelper.getESClient()
				.prepareSearch(INDEX_NAME)
		        .setTypes(TYPE_NAME)
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//		        .setQuery(QueryBuilders.prefixQuery("name", name))
		        .setPostFilter(QueryBuilders.fuzzyQuery("name", name))
		        .addSort(SortBuilders.fieldSort("age").order(SortOrder.DESC));
		
		SearchResponse response = builder.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		
		for(SearchHit hit : hits){
			String userJSON = hit.getSourceAsString();
			UserModel um = 
					JSONObject.parseObject(userJSON, UserModel.class);
			
			System.out.println("um="+um);
			users.add(um);
		}
		
		return users;
	}
	
	
	public static void matchDemo(String matchValue){
		/*
		 *  match
			match_phrase
			match_phrase_prefix
			match_all
			multi_match

		 */
		SearchRequestBuilder builder = ESHelper.getESClient()
				.prepareSearch(INDEX_NAME)
		        .setTypes(TYPE_NAME)
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
//		        .setQuery(QueryBuilders.multiMatchQuery(matchValue, "strs","name"));
		        // 演示match_all
//		        .setQuery(QueryBuilders.matchAllQuery());
		        // 演示match_phrase_prefix
//		        .setQuery(QueryBuilders.matchPhrasePrefixQuery("strs", matchValue));
		        // 演示match
//		        .setQuery(QueryBuilders.matchQuery("strs", matchValue));
		        // 演示match operator : or|and 
		        .setQuery(QueryBuilders.orQuery(
		        			QueryBuilders.termQuery("strs", matchValue),
		        			QueryBuilders.termQuery("uuid", matchValue)
		        		));
		        
		System.out.println("builder="+builder);
		
		SearchResponse response = builder.execute().actionGet();
		SearchHit[] hits = response.getHits().getHits();
		
		for(SearchHit hit : hits){
			String userJSON = hit.getSourceAsString();
			UserModel um = 
					JSONObject.parseObject(userJSON, UserModel.class);
			
			System.out.println("um="+um);
		}
	}
	
	
	public static void aggsDemo(){
		SearchRequestBuilder builder = ESHelper.getESClient()
				.prepareSearch(INDEX_NAME)
		        .setTypes(TYPE_NAME)
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.matchAllQuery())
		        .addAggregation(AggregationBuilders.terms("aggs_terms_strs").field("strs"));
//		        .addAggregation(AggregationBuilders.stats("aggs_stats_age").field("age"));
		        // min
//		        .addAggregation(AggregationBuilders.min("aggs_min_age").field("age"));
		        // 获取avg
//		        .addAggregation(AggregationBuilders.avg("aggs_avg_age").field("age"));
		        
		System.out.println("builder="+builder);
		// 获取聚合参数

		SearchResponse response = builder.execute().actionGet();
		Aggregations aggs = response.getAggregations();
		// 获取avg参数
//		Avg avg = aggs.get("aggs_age");
//		System.out.println("avg="+avg.getValueAsString());
		
		// min、max
//		Min min = aggs.get("aggs_min_age");
//		System.out.println("min="+min.getValue());
		
		// stats
//		Stats stats = aggs.get("aggs_stats_age");
//		System.out.println("stats="+stats.getCount());
		
		// terms
		StringTerms terms = aggs.get("aggs_terms_strs");
		for(int i=0;i<terms.getBuckets().size();i++){
			String term = terms.getBuckets().get(i).getKeyAsString();
			System.err.println("term="+term);
			System.out.println("counts="+terms.getBuckets().get(i).getDocCount());
		}
		
	}
	
	
	public static void main(String[] args) {
//		matchDemo("qq");
		aggsDemo();
//		List<String> strs = new ArrayList<String>();
//		strs.add("allen3");
//		strs.add("green3");
//		strs.add("qq3");
//		
//		UserModel um = new UserModel();
//		um.setUuid("003");
//		um.setName("Allen3");
//		um.setAge(17);
//		um.setStrs(strs);
//		
//		CRUDTest.addModel(um);
	}
	
}
