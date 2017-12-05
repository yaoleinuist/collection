package com.lzhsite.es.spring.search;

import java.util.List;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.metrics.avg.Avg;
import org.elasticsearch.search.sort.SortBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.lzhsite.es.spring.model.UserModel;

@Service("searchDemo")
public class SpringSearchDemo {
	@Autowired
	private ElasticsearchTemplate et;
	
	private static final String INDEX_NAME="ibeifeng-java";
	private static final String TYPE_NAME="javaapi";
	
	
	// DSL
	public void dslDemo(){

		// searchQuery
//		SearchQuery query = new NativeSearchQuery(
//				QueryBuilders.termQuery("name", "allen")
//				);
		
		// 演示分页 
//		SearchQuery query = new NativeSearchQueryBuilder()
//					.withQuery(QueryBuilders.matchAllQuery())
//					.withPageable(new PageRequest(1, 1))
//					.build();
		
		// 演示排序
		SearchQuery query = new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.matchAllQuery())
				.withSort(SortBuilders.fieldSort("age"))
				.build();
		
		
		List<UserModel> users = et.queryForList(query, UserModel.class);
		for(UserModel um : users){
			System.out.println("um="+um);
		}
	}
	
	// 全文检索
	public void matchDemo(){

		// 演示排序
		SearchQuery query = new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.matchQuery("strs", "qq"))
				.withSort(SortBuilders.fieldSort("age"))
				.build();
		
		
		List<UserModel> users = et.queryForList(query, UserModel.class);
		for(UserModel um : users){
			System.out.println("um="+um);
		}
	}
	
	// 聚合
	public void aggsDemo(){

		SearchQuery query = new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.matchQuery("strs", "qq"))
				.withSort(SortBuilders.fieldSort("age"))
				.addAggregation(AggregationBuilders.avg("agg_avg_age").field("age"))
				.build();
		
		// 这里是最大的区别
		// 如何获取原生态的searchResponse
		et.queryForPage(query, UserModel.class, new SearchResultMapper() {
			
			@Override
			public <T> Page<T> mapResults(SearchResponse response, Class<T> cls, Pageable pageAble) {

				Avg avg = response.getAggregations().get("agg_avg_age");
				System.out.println("avg="+avg.getValueAsString());
				
				return null;
			}
		});
	}
	
	
	public static void main(String[] args) {
		
		ApplicationContext atc = new ClassPathXmlApplicationContext("applicationContext-es.xml");
		SpringSearchDemo demo = (SpringSearchDemo)atc.getBean("searchDemo");
		// DSL运行
//		demo.dslDemo();
		// Match运行
//		demo.matchDemo();
		
		// aggs运行
		demo.aggsDemo();
		
	}
}
