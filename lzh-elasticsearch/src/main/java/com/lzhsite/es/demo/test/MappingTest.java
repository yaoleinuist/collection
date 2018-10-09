package com.lzhsite.es.demo.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.lzhsite.es.util.ElasticSearchHandler;

public class MappingTest {
	 

		public static void main(String[] args) throws IOException {
			Client client = ElasticSearchHandler.getInstance();
			// 索引可能有多个类型，每个有自己的映射，不同类型的文档可以存放在同一个索引中
			File article = new File("D:/java：eclipse/article_mapping2.txt");
			FileReader fr = new FileReader(article);
			BufferedReader bfr = new BufferedReader(fr);
			String mapping_json = null;
	 
			if ((mapping_json = bfr.readLine()) != null) {
				// create indexname:if not exit index
				// client.admin().indices().prepareCreate("index_name").execute().actionGet();
				// notice: typename==mapping_json.name
				client.admin().indices().preparePutMapping("index_name")
						.setType("index_type").setSource(mapping_json)
						.execute().actionGet();
				 System.out.println(mapping_json);
			}
			fr.close();
	}
		
		  public static XContentBuilder getMappingByData(Map<String, Object> dataMap) {
		        XContentBuilder mapping = null;
		        try {
		            mapping = XContentFactory.jsonBuilder().startObject()
		                    .startObject("properties");


		            if (!dataMap.isEmpty()) {
		                for (Map.Entry<String, Object> proName : dataMap.entrySet()) {
		                    if (null != proName.getKey()) {
		                        mapping.startObject(proName.getKey());
		                        if (proName.getValue() instanceof LocalDateTime) {
		                            mapping.field("type", "date").field("index", "not_analyzed");
		                        } else if (proName.getValue() instanceof String) {
		                            mapping.field("type", "text").field("index", "not_analyzed");
		                        } else if (proName.getValue() instanceof Long) {
		                            mapping.field("type", "long").field("index", "not_analyzed");
		                        } else if (proName.getValue() instanceof Integer) {
		                            mapping.field("type", "integer").field("index", "not_analyzed");
		                        } else if (proName.getValue() instanceof Short) {
		                            mapping.field("type", "short").field("index", "not_analyzed");
		                        }

		                        mapping.endObject();
		                    }
		                }
		            }
		            mapping.endObject().endObject();

		        } catch (IOException e) {
		          
		        }

		        return mapping;
		    }

}
