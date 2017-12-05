package com.lzhsite.es.util;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

//https://www.cnblogs.com/zlslch/p/6474424.html
//http://blog.csdn.net/jek123456/article/details/67639268
public class ElasticSearchHandler {

    private static Log logger = LogFactory.getLog(ElasticSearchHandler.class);
	private static TransportClient client = null;
	
    private static ElasticSearchHandler esHelper;
	
    private ElasticSearchHandler() {
        //使用本机做为节点
        this(ResourceBundle.getBundle("props.vip").getString("elasticSearch_ips"));
    }

    
	 
    private  ElasticSearchHandler(String ipAddresses){
		try{
	        Settings settings =  Settings.settingsBuilder().put("cluster.name", "elasticsearch")
	        	    .put("node.name", "es1")
	                .put("client.transport.ignore_cluster_name", false)
	                .put("node.client", true)
	                .put("client.transport.sniff", true)
	                .put("client.transport.ping_timeout", "60s").build();
	        
			client= TransportClient.builder().settings(settings).build();
			
		    String[] ipAddress = ipAddresses.split(",");
	        for (String ip : ipAddress) {
	        	client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipAddresses), 9300));
	        }
	        logger.info("elasticsearch client 被创建了");
 
		}catch(Exception e){
			e.printStackTrace();
		}
 
	}
	
	public static void closeESClient(){
		if(client!=null){
			client.close();
		}
	}
	
	
    public static Client getInstance(){
        if (null == esHelper){
            synchronized(ElasticSearchHandler.class){
                if (null == esHelper){
                	esHelper = new ElasticSearchHandler("192.168.226.3");
                }
            }
        }
        return client;
    }
	
	 /**
     * 单例模式
     * @param ipAddresses
     * @return
     */
    public static ElasticSearchHandler getInstance(String ipAddresses){
        if (null == esHelper){
            synchronized(ElasticSearchHandler.class){
                if (null == esHelper){
                	esHelper = new ElasticSearchHandler(ipAddresses);
                }
            }
        }
        return esHelper;
    }

    public void close() {
        client.close();
    }

    public void replaceIndexBatch(String indexName, String indexType, List<String> jsonDatas, List<String> ids) throws Exception {
        if (ids == null) {
            this.replaceIndexResponse(indexName, indexType, jsonDatas);
        } else {
            this.replaceIndexResponse(indexName, indexType, jsonDatas, ids);
            ids.clear();
        }
        jsonDatas.clear();
//        this.close();
    }

    /**
     * 建立索引,索引建立好之后,会在elasticsearch-0.20.6\data\elasticsearch\nodes\0创建索引 
     *
     * @param indexName 为索引库名，一个es集群中可以有多个索引库。 名称必须为小写
     * @param indexType Type为索引类型，是用来区分同索引库下不同类型的数据的，一个索引库下可以有多个索引类型。
     * @param jsonDatas json格式的数据集合
     * @param ids       索引Id
     * @return
     */
    public void replaceIndexResponse(String indexName, String indexType, List<String> jsonDatas, List<String> ids) throws Exception {
        if (jsonDatas.size() == 0 || ids.size() == 0) {
            return;
        }
        if (jsonDatas.size() != ids.size()) {
            throw new Exception();
        }
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (int i = 0; i < jsonDatas.size(); i++) {
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, indexType, ids.get(i));
            indexRequestBuilder.setSource(jsonDatas.get(i));
            bulkRequest.add(indexRequestBuilder);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
        	   throw new Exception();
        }
    }

    public void replaceIndexResponse(String indexName, String indexType, List<String> jsonDatas) throws  Exception{
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (int i = 0; i < jsonDatas.size(); i++) {
            IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName, indexType);
            indexRequestBuilder.setSource(jsonDatas.get(i));
            bulkRequest.add(indexRequestBuilder);
        }
        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            throw new Exception();
        }
    }

    /**
     * 建立索引,索引建立好之后,会在elasticsearch-0.20.6\data\elasticsearch\nodes\0创建索引 
     *
     * @param indexName 为索引库名，一个es集群中可以有多个索引库。 名称必须为小写
     * @param indexType Type为索引类型，是用来区分同索引库下不同类型的数据的，一个索引库下可以有多个索引类型。
     * @param jsondata  json格式的数据集合
     * @return
     */
    public IndexResponse replaceIndexResponse(String indexName, String indexType, String jsondata) {
        IndexResponse response = client.prepareIndex(indexName, indexType)
                .setSource(jsondata)
                .execute()
                .actionGet();
        return response;
    }

    /***
     *
     * @param indexName
     * @param indexType
     * @param jsondata
     * @param indexUrl
     * @param typeUrl
     * @param typeDdl
     * @return
     */
    public IndexResponse replaceIndexResponse(String indexName, String indexType, String jsondata, String indexUrl, String typeUrl, String typeDdl) throws Exception {
        try {
            if (!indexExist(indexName)) {
                createIndex(indexUrl);
            }
            if (!typeExist(indexName, indexType)) {
                createType(typeUrl, typeDdl);
            }
        } catch (IOException e) {
            throw new Exception();
        }
        IndexResponse response = replaceIndexResponse(indexName, indexType, jsondata);
        return response;
    }


    /***
     * @param indexUrl "http://root:123456..@192.168.181.168:9200/test"
     */
    public void createIndex(String indexUrl) throws IOException {
        HttpPut request = new HttpPut(indexUrl);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(request);
        httpclient.close();
    }

    /***
     * @param typeUrl "http://root:123456..@192.168.181.168:9200/test/angel4/_mapping"
     * @param typeDdl "{\"angel4\":{\"properties\":{\"created\":{\"type\":\"multi_field\",\"fields\":{\"created\":{\"type\":\"string\"},\"date\":{\"type\":\"date\"}}}}}}"
     */
    public void createType(String typeUrl, String typeDdl) throws IOException {
        HttpPut request = new HttpPut(typeUrl);
        String typeDdlFmt = typeDdl.replaceAll("\\s*", "");
        request.setEntity(new StringEntity(typeDdlFmt));
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(request);
        httpclient.close();
    }

    /***
     * @param indexUrl "http://root:123456..@192.168.181.168:9200/test"
     */
    public CloseableHttpResponse createIndexResponse(String indexUrl) throws IOException {
        HttpPut request = new HttpPut(indexUrl);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {

            response = httpclient.execute(request);

        }catch (Exception e){
            throw e;
        }finally {
            httpclient.close();
        }

        return response;
    }

    /***
     * @param typeUrl "http://root:123456..@192.168.181.168:9200/test/angel4/_mapping"
     * @param typeDdl "{\"angel4\":{\"properties\":{\"created\":{\"type\":\"multi_field\",\"fields\":{\"created\":{\"type\":\"string\"},\"date\":{\"type\":\"date\"}}}}}}"
     */
    public CloseableHttpResponse createTypeWithResponse(String typeUrl, String typeDdl) throws IOException {
        HttpPut request = new HttpPut(typeUrl);
        String typeDdlFmt = typeDdl.replaceAll("\\s*", "");
        request.setEntity(new StringEntity(typeDdlFmt));
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {

            response = httpclient.execute(request);

        }catch (Exception e){
            throw e;
        }finally {
            httpclient.close();
        }

        return response;
    }





    public boolean indexExist(String indexName) {
        boolean indexExists = client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
        return indexExists;
    }

    public boolean typeExist(String indexName, String typeName) {
        boolean indexExists = client.admin().indices().prepareTypesExists(indexName).setTypes(typeName).execute().actionGet().isExists();
        return indexExists;
    }
	
}
