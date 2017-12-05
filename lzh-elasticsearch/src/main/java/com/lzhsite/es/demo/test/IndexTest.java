package com.lzhsite.es.demo.test;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;

import com.lzhsite.es.util.ElasticSearchHandler;

public class IndexTest {

	// 创建索引
	public static void createIndex(String indexName){
		CreateIndexRequest create = new CreateIndexRequest(indexName);
		ElasticSearchHandler.getInstance().admin().indices().create(create);
	}
	
	// 删除索引
	public static void deleteIndex(String indexName){
		DeleteIndexRequest delete = new DeleteIndexRequest(indexName);
		ElasticSearchHandler.getInstance().admin().indices().delete(delete);
	}
	
	public static void main(String[] args) {
		String indexName = "ibeifeng-java";
		createIndex(indexName);
//		deleteIndex(".*");
	}
	
}
