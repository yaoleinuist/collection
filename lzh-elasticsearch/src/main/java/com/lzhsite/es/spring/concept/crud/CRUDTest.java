package com.lzhsite.es.spring.concept.crud;

import java.util.List;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.data.elasticsearch.core.query.UpdateQueryBuilder;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.lzhsite.es.spring.model.UserModel;


@Service(value="CRUDTest")
public class CRUDTest {

	@Autowired
	private ElasticsearchTemplate et;
	private static final String INDEX_NAME="ibeifeng-java";
	private static final String TYPE_NAME="javaapi";
	
	// 新增
	public void addModel(UserModel um){
		// 将对象组装成json字符串
		String userJson = JSON.toJSONString(um);
		
		// 将数据放入ES中
		et.index(new IndexQueryBuilder()
				.withIndexName(INDEX_NAME)
				.withType(TYPE_NAME)
				.withId(um.getUuid())
				.withSource(userJson)
				.build());
	}
	// 修改
	public void updateModel(UserModel um) throws Exception{
		// 拼装json字符串
		String userJson = JSON.toJSONString(um);
		IndexRequest request = new IndexRequest();
		request.source(userJson);
		
		UpdateQuery update = new UpdateQueryBuilder()
				.withIndexName(INDEX_NAME)
				.withType(TYPE_NAME)
				.withId(um.getUuid())
				.withIndexRequest(request)
				.withClass(UserModel.class)
				.build();
		
		
		et.update(update);
		
	}
	
	// 删除
	public void deleteModel(String uuid){
		DeleteQuery deleteQuery = new DeleteQuery();
		deleteQuery.setIndex(INDEX_NAME);
		deleteQuery.setType(TYPE_NAME);
		deleteQuery.setQuery(QueryBuilders.termQuery("uuid", uuid));
		
		et.delete(deleteQuery);
		
	}
	
	public void queryUser(){
		// StringQuery
		StringQuery query = new StringQuery(QueryBuilders.matchAllQuery().toString());
		List<UserModel> users = et.queryForList(query, UserModel.class);
		for(UserModel um : users){
			System.out.println("um="+um);
		}
		
		// searchQuery
//		SearchQuery query = new NativeSearchQuery(
//				QueryBuilders.termQuery("age", 30)
//				);
//		
//		List<UserModel> users = et.queryForList(query, UserModel.class);
//		for(UserModel um : users){
//			System.out.println("um="+um);
//		}
	}
	
}
