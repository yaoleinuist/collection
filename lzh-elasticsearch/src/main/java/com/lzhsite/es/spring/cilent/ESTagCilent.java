package com.lzhsite.es.spring.cilent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.MetricsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lzhsite.core.es.EsOperateSdk;
import com.lzhsite.core.utils.DateUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/*
 * 序号	覆盖功能例子
	1	客户端链接初始化
	2	filte无评分查询用法
	3	query有评分查询用法
	4	单字段分组用法
	5	多字段分组用法
	6	读取有索引无存储数据的用法
	7	设置指定字段返回
*/
@Component
public class ESTagCilent {

	@Autowired
	private EsOperateSdk esOperateSdk;

	public JSONObject getUserInfoList(String productid, String startdate, String stopdate) throws Exception {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("product_id", productid));
		boolQueryBuilder.must(QueryBuilders.rangeQuery("localtime").from(startdate).to(stopdate));

		SearchResponse response = esOperateSdk.esClient().prepareSearch("razor_cd").setTypes("clientdata")
				.setQuery(boolQueryBuilder)
				/*
				 * .addAggregation(
				 * AggregationBuilders.terms("agg").field("user_id").size(20) )
				 */
				.addAggregation(AggregationBuilders.cardinality("agg1").field("user_id"))
				// .addAggregation(AggregationBuilders.dateRange("range1").field("localtime").addRange(startdate,stopdate))
				.addSort("localtime", SortOrder.DESC).setSize(2000).execute().actionGet();

		JSONObject object = new JSONObject();
		object.put("total", response.getAggregations().getProperty("agg1.value").toString());

		JSONArray arr = new JSONArray();
		// 取前20条不重复的userid
		List<String> userIds = new ArrayList<String>();
		SearchHit[] hits = response.getHits().getHits();
		for (SearchHit hit : hits) {
			if (userIds.size() < 20) {
				String userid = hit.getSource().get("user_id").toString();
				int flag = 1;
				for (String s : userIds) {
					if (s.equals(userid)) {
						flag = 0;
						break;
					}
				}
				if (flag == 1)// flag为1表示还没有记录该用户
				{
					userIds.add(userid);
					arr.add(hit.getSourceAsString());
				}

			}
		}

		object.put("userList", arr);
		// System.out.println(object);
		return object;
	}

	public JSONObject getUserInfoListById(String productid, String startdate, String stopdate, String userid)
			throws Exception {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("product_id", productid));
		boolQueryBuilder.must(QueryBuilders.regexpQuery("user_id", ".*" + userid + ".*"));
		boolQueryBuilder.must(QueryBuilders.rangeQuery("localtime").from(startdate).to(stopdate));

		SearchResponse response = esOperateSdk.esClient().prepareSearch("razor_cd").setTypes("clientdata")
				.setQuery(boolQueryBuilder) // Filter
				/*
				 * .addAggregation(
				 * AggregationBuilders.terms("agg").field("user_id").size(20) )
				 */
				.addAggregation(AggregationBuilders.cardinality("agg1").field("user_id"))
				.addSort("localtime", SortOrder.DESC).setSize(2000).execute().actionGet();

		JSONObject object = new JSONObject();
		object.put("total", response.getAggregations().getProperty("agg1.value").toString());

		JSONArray arr = new JSONArray();
		// 取前20条不重复的userid
		List<String> userIds = new ArrayList<String>();
		SearchHit[] hits = response.getHits().getHits();
		for (SearchHit hit : hits) {
			if (userIds.size() < 20) {
				String user_id = hit.getSource().get("user_id").toString();
				int flag = 1;
				for (String s : userIds) {
					if (s.equals(user_id)) {
						flag = 0;
						break;
					}
				}
				if (flag == 1)// flag为1表示还没有记录该用户
				{
					userIds.add(user_id);
					arr.add(hit.getSourceAsString());
				}

			}
		}

		object.put("userList", arr);

		// System.out.println(object);
		return object;
	}

	public JSONObject getUserInfoListByTags(String productid, String tags) throws Exception {
		String[] tags_arr = tags.split(";");
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("product_id", productid));
		for (String tag : tags_arr) {
			if (tag.contains("c_channel_")) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("channel_id", tag.substring(10)));
			} else if (tag.contains("c_provinc_")) {
				boolQueryBuilder.must(QueryBuilders.matchQuery("region", tag.substring(10)));
			} else {
				boolQueryBuilder.must(QueryBuilders.matchQuery("app_tags.tag_name", tag));
			}

		}

		SearchResponse response = esOperateSdk.esClient().prepareSearch("razor_tag").setTypes("user_tag")
				.setQuery(boolQueryBuilder)
				// .addField("user_id")
				.setSize(20).execute().actionGet();

		// System.out.println(response.toString());

		// 根据查询出的用户id去clientdata中二次查询详细信息
		JSONObject object = new JSONObject();
		object.put("total", response.getHits().getTotalHits());

		JSONArray arr = new JSONArray();
		// 取前20条不重复的userid
		List<String> userIds = new ArrayList<String>();
		SearchHit[] hits = response.getHits().getHits();
		for (SearchHit hit : hits) {
		}

		return object;
	}

	public String getUserDetailTags(String productid, String userid) throws Exception {

		GetResponse usertag = esOperateSdk.esClient().prepareGet("razor_tag", "user_tag", userid + "|" + productid)
				.get();
		return usertag.getSourceAsString();
	}

	public JSONArray getUserDetail(String productid, String startdate, String stopdate, String userid)
			throws Exception {

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("product_id", productid));
		boolQueryBuilder.must(QueryBuilders.regexpQuery("user_id", userid));
		boolQueryBuilder.must(QueryBuilders.rangeQuery("localtime").from(startdate).to(stopdate));

		SearchResponse response = esOperateSdk.esClient().prepareSearch("razor_cd").setTypes("clientdata")
				.setQuery(boolQueryBuilder) // Filter
				.addSort("localtime", SortOrder.DESC).setSize(20).execute().actionGet();

		// Map<String,List<String>> map=new HashMap<String, List<String>>();
		JSONArray arr = new JSONArray();

		SearchHit[] hits = response.getHits().getHits();
		String tmp = "";
		int flag = 1;

		for (SearchHit hit : hits) {
			System.out.println(hit.getSourceAsString());
			JSONObject jsonObject = new JSONObject();
			String sessionid = hit.getSource().get("session_id").toString();
			if (flag == 1) // 表示取第一条记录
			{
				tmp = sessionid;
				flag = 0;
			} else { // 取第1条之后的记录
				if (tmp.equals(sessionid)) // 重复的sessionid，直接结束，进入下次循环
				{
					continue;
				} else {
					tmp = sessionid;
				}
			}

			// 1.添本次session的clientdata数据
			jsonObject.put("clientdata", hit.getSourceAsString());
			// 2.添本次session的usinglog
			GetResponse usinglog = esOperateSdk.esClient()
					.prepareGet("razor_usinglog", "usinglog", userid + "|" + sessionid).get();
			jsonObject.put("usinglog", usinglog.getSourceAsString());
			// 3.添本次session的event
			GetResponse event = esOperateSdk.esClient().prepareGet("razor_event", "event", userid + "|" + sessionid)
					.get();
			jsonObject.put("event", event.getSourceAsString());

			arr.add(jsonObject);
		}

		return arr;
	}

	// AggregationBuilders.cardinality用法还没懂
	public String getTagTop(String productid) throws Exception {

		// select app_tags.tag_name,count(*) as agg
		// from user_tag
		// where product_id=xxx
		// group by app_tags.tag_name
		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("product_id", productid));
		// boolQueryBuilder.must(QueryBuilders.rangeQuery("add_time").from(startdate).to(stopdate));

		SearchResponse response = esOperateSdk.esClient().prepareSearch("razor_tag").setTypes("user_tag")
				.setQuery(boolQueryBuilder)
				.addAggregation(AggregationBuilders.terms("agg").field("app_tags.tag_name").size(20))
				.addAggregation(AggregationBuilders.cardinality("agg1").field("app_tags.tag_name")).setSize(0).execute()
				.actionGet();

		return response.toString();
	}

	/***
	 * 获取search请求的结果，并输出打印结果信息
	 * 
	 * @param search
	 * @throws Exception
	 */
	public static void showResult(SearchRequestBuilder search) throws Exception {
		SearchResponse r = search.get();// 得到查询结果
		for (SearchHit hits : r.getHits()) {
			// 只能获取addFields里面添加的字段
			// System.out.println(hits.getFields().get("userId").getValue());
			// 默认可会source里面获取所需字段
			System.out.println(hits.getSource().get("actId"));
			// 注意不支持data.subjectName这样的访问方式
			// System.out.println(hits.getId()+" "+hits.score()+"
			// "+data.get("subjectName"));
			// 如果是个嵌套json，需要转成map后，访问其属性
			// Map data=(Map) hits.getSource().get("data");
			// System.out.println(hits.getId()+" "+hits.score()+"
			// "+data.get("subjectName"));

		}
		long hits = r.getHits().getTotalHits();// 读取命中数量
		System.out.println(hits);
	}

	/***
	 * 最新版elasticsearch2.3的query测试，结果会评分
	 * 
	 * @throws Exception
	 */
	public void testQuery() throws Exception {
		SearchRequestBuilder search = esOperateSdk.esClient().prepareSearch("userlog*").setTypes("logs");
		String subjectName = "语文";
		// 注意查询的时候，支持嵌套的json查询，通过点符号访问下层字段，读取结果时不支持这种方式
		search.setQuery(QueryBuilders.queryStringQuery("+data.subjectName:* -data.subjectName:" + subjectName + "  "));
		showResult(search);
	}

	/***
	 * 最新版的elasticsearch2.3的filterquery测试，结果不会评分
	 * 
	 * @throws Exception
	 */
	public void testFilter() throws Exception {
		SearchRequestBuilder search = esOperateSdk.esClient().prepareSearch("userlog*").setTypes("logs");
		// 第一个参数包含的字段数组，第二个字段排除的字段数组
		// search.setFetchSource(new String[]{"userId","actId"},null);
		// search.addFields("actId","userId"); //另一种写法
		String schoolName = "沙河市第三小学";
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery().must(QueryBuilders.queryStringQuery("*:*"))
				.filter(QueryBuilders.queryStringQuery("+data.subjectName:* +schoolName:" + schoolName)
						.defaultOperator(Operator.valueOf("AND")));
		// 设置query
		search.setQuery(boolQuery);
		// 打印结果数据
		showResult(search);
	}

	/***
	 * 两个字段分组测试，在时间的维度上加上任意其他的字段聚合，类似group by field1,field2
	 * 
	 * @throws Exception
	 */
	public void testTwoAggString() throws Exception {
		
		
		// Select @timestamp,count(@timestamp)
		// from logs where timestamp in (now(),adddate(now(),123)) 
		// Group by @timestamp,module
		
		
		// 构造search请求
		SearchRequestBuilder search = esOperateSdk.esClient().prepareSearch("userlog*").setTypes("logs");
		search.setQuery(QueryBuilders.queryStringQuery(
				"@timestamp:[ " + new Date().getTime() + " TO " + DateUtils.addMins(new Date(), 123).getTime() + "}"));
		// 一级分组字段
		DateHistogramBuilder dateagg = AggregationBuilders.dateHistogram("dateagg");
		dateagg.field("@timestamp");// 聚合时间字段
		dateagg.interval(DateHistogramInterval.HOUR);// 按小时聚合
		dateagg.format("yyyy-MM-dd HH"); // 格式化时间
		dateagg.timeZone("Asia/Shanghai");// 设置时区，注意如果程序部署在其他国家使用时，使用Joda-Time来动态获取时区
										 // new DateTime().getZone()


		// 二级分组字段
		TermsBuilder twoAgg = AggregationBuilders.terms("stragg").field("module");
		// 组装聚合字段
		dateagg.subAggregation(twoAgg);
		// 向search请求添加
		search.addAggregation(dateagg);
		// 获取结果
		SearchResponse r = search.get();
		Histogram h = r.getAggregations().get("dateagg");
		// 得到一级聚合结果里面的分桶集合
		List<Histogram.Bucket> buckets = (List<Histogram.Bucket>) h.getBuckets();
		// 遍历分桶集
		for (Histogram.Bucket b : buckets) {
			// 读取二级聚合数据集引用
			Aggregations sub = b.getAggregations();
			// 获取二级聚合集合
			StringTerms count = sub.get("stragg");
			// 如果设置日期的format的时候，需要使用keyAsString取出，否则获取的是UTC的标准时间
			System.out.println(b.getKeyAsString() + "  " + b.getDocCount());
			System.out.println("=============================================");
			for (Terms.Bucket bket : (List<Terms.Bucket>) count.getBuckets()) {

				System.out.println(bket.getKeyAsString() + "  " + bket.getDocCount());
			}
			System.out.println("************************************************");

		}

	}

	/***
	 * @param field 聚合的字段测试
	 * @throws Exception
	 */
	public void testOneAggString() throws Exception {
 
		// 构造search请求
		SearchRequestBuilder search = esOperateSdk.esClient().prepareSearch("ibeifeng_aggs*").setTypes("infos");
		// 聚合构造
		TermsBuilder termsBuilder = AggregationBuilders.terms("aggs_terms").field("title");
		// 添加到search请求
		search.addAggregation(termsBuilder);
		// 获取结果
		SearchResponse searchResponse = search.get();
		// 获取agg标识下面的结果
		Terms agg1 = searchResponse.getAggregations().get("aggs_terms");
		// 获取bucket
		List<Terms.Bucket> buckets = (List<Terms.Bucket>) agg1.getBuckets();
		long sum = 0;
		for (Terms.Bucket b : buckets) {
			Aggregations sub = b.getAggregations();
			System.out.println(b.getKeyAsString() + "  " + b.getDocCount());
			sum += b.getDocCount();
		}
		//class  3
		//ibeifeng  3
		//01  1
		//02  1
		//03  1
		//总数：9
		System.out.println("总数：" + sum);
	}
	
	 


	/***
	 * 每一天的select count(distinct(actid)) from talbe group by date
	 */
	public void countDistinctByField() {

		// 构造search请求
		SearchRequestBuilder search = esOperateSdk.esClient().prepareSearch("userlog*").setTypes("logs");
		search.setQuery(QueryBuilders.queryStringQuery(
				"@timestamp:[ " + new Date().getTime() + " TO " + DateUtils.addMins(new Date(), 123).getTime() + "}"));
		search.setSize(0);
		// 一级分组字段
		DateHistogramBuilder dateagg = AggregationBuilders.dateHistogram("dateagg");
		dateagg.field("@timestamp");// 聚合时间字段
		// dateagg.interval(DateHistogramInterval.HOUR);//按小时聚合
		dateagg.interval(DateHistogramInterval.DAY);// 按天聚合
		// dateagg.format("yyyy-MM-dd HH"); //格式化时间
		dateagg.format("yyyy-MM-dd"); // 格式化时间
		dateagg.timeZone("Asia/Shanghai");// 设置时区，注意如果程序部署在其他国家使用时，使用Joda-Time来动态获取时区
											// new DateTime().getZone()

		// 二级分组字段
		// TermsBuilder twoAgg =
		// AggregationBuilders.terms("stragg").field("actId");
		MetricsAggregationBuilder twoAgg = AggregationBuilders.cardinality("stragg").field("actId");

		// 组装聚合字段
		dateagg.subAggregation(twoAgg);
		// 向search请求添加
		search.addAggregation(dateagg);
		// 获取结果
		SearchResponse r = search.get();
		Histogram h = r.getAggregations().get("dateagg");
		// 得到一级聚合结果里面的分桶集合
		List<Histogram.Bucket> buckets = (List<Histogram.Bucket>) h.getBuckets();
		// 遍历分桶集
		for (Histogram.Bucket b : buckets) {
			// 读取二级聚合数据集引用
			Aggregations sub = b.getAggregations();
			// 获取二级聚合集合
			Cardinality agg = sub.get("stragg");
			// 获取去重后的值
			long value = agg.getValue();
			// 如果设置日期的format的时候，需要使用keyAsString取出，否则获取的是UTC的标准时间
			System.out.println(b.getKeyAsString() + "  " + b.getDocCount() + " " + value);
		}
	}

}


