package com.lzhsite.es.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lzhsite.es.spring.crud.UserModel;

public class CRUDTest {

	private static final String INDEX_NAME="ibeifeng-java";
	private static final String TYPE_NAME="javaapi";
	
	// 新增数据
	public static void addModel(UserModel um){
		
		String userJson = JSON.toJSONString(um);
		IndexResponse response = ElasticSearchHandler.getInstance()
				.prepareIndex(INDEX_NAME, TYPE_NAME,um.getUuid())
		        .setSource(userJson)
		        .get();
		
		// 以下是一些相关信息
		// Index name
		String _index = response.getIndex();
		// Type name
		String _type = response.getType();
		// Document ID (generated or not)
		String _id = response.getId();
		// Version (if it's the first time you index this document, you will get: 1)
		long _version = response.getVersion();
		// isCreated() is true if the document is a new one, false if it has been updated
		boolean created = response.isCreated();
		
		System.out.println("_index="+_index+" ,_type="+_type+" ,_version="+_version);
	}
	
	// 修改数据
	public static void updateModel(UserModel um){
		String userJson = JSON.toJSONString(um);
		UpdateRequest updateRequest = new UpdateRequest(INDEX_NAME, TYPE_NAME, um.getUuid())
		        .doc(userJson);
		try {
			ElasticSearchHandler.getInstance().update(updateRequest).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
	
	// 删除数据
	public static void deleteModel(String uuid){
		DeleteResponse response = 
				ElasticSearchHandler.getInstance()
				.prepareDelete(INDEX_NAME, TYPE_NAME, uuid)
				.get();
	}
	
	// 根据主键查询
	public static UserModel getByUuid(String uuid){
		GetResponse response = 
				ElasticSearchHandler.getInstance()
				.prepareGet(INDEX_NAME, TYPE_NAME, uuid)
				.get();
		
		String userJSON = response.getSourceAsString();
		
		UserModel um = (UserModel) JSONObject.parseObject(userJSON, UserModel.class);
		return um;
	}
	
	// 查询全部
	public static List<UserModel> getAll(){
		List<UserModel> users = new ArrayList<UserModel>();
		SearchResponse response = ElasticSearchHandler.getInstance()
				.prepareSearch(INDEX_NAME)
				.setTypes(TYPE_NAME)
		        .execute()
		        .actionGet();
		
		SearchHit[] hits = response.getHits().getHits();
		for(SearchHit hit : hits){
			String userJSON = hit.getSourceAsString();
			UserModel um = 
					JSONObject.parseObject(userJSON, UserModel.class);
			users.add(um);
		}
		
		return users;
	}
	
	// 根据条件查询
	public static List<UserModel> getByCondition(String name){
		List<UserModel> users = new ArrayList<UserModel>();
		SortBuilder sb = SortBuilders.fieldSort("");
		
//		curl -XGET http://192.168.21.131:9200/index/fulltext/_search -d'
//		{
//			"query":{
//				"term":{"content":"ibeifeng"}
//			}
//		}'
		SearchResponse response = ElasticSearchHandler.getInstance()
				.prepareSearch(INDEX_NAME)
		        .setTypes(TYPE_NAME)
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery("name", name))                 // Query
		        .execute()
		        .actionGet();
		
		SearchHit[] hits = response.getHits().getHits();
		for(SearchHit hit : hits){
			String userJSON = hit.getSourceAsString();
			UserModel um = 
					JSONObject.parseObject(userJSON, UserModel.class);
			users.add(um);
		}
		
		return users;
	}
	
	public static void test(){
		SortBuilders.fieldSort("");
	}
	
}
