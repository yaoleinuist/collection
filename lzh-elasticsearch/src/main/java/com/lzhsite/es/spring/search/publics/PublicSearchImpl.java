package com.lzhsite.es.spring.search.publics;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.lzhsite.es.spring.model.GoodsDescModel;
import com.lzhsite.es.spring.model.GoodsModel;

@Service("publicSearchImpl")
public class PublicSearchImpl implements PublicSearch{

	@Autowired
	private ElasticsearchTemplate et;
	private static final String INDEX_NAME="ibeifeng-project";
	private static final String TYPE_NAME="search";
	
	/*
	 * 1、商品供应商 -》 需要把店铺相关内容填写完整
	 * 2、促销活动 -》 热搜的一些内容填写完整
	 * 3、商品维护专员
	 * 4、大数据的挖掘
	 * @see com.es.spring.publics.search.PublicSearch#addKeyWord(com.es.spring.publics.search.GoodsDescModel)
	 */
	@Override
	public void addKeyWord(GoodsDescModel gm) {
		// 将对象组装成json字符串
		String goodsDescJson = JSON.toJSONString(gm);
		
		// 将数据放入ES中
		et.index(new IndexQueryBuilder()
				.withIndexName(INDEX_NAME)
				.withType(TYPE_NAME)
				.withId(""+gm.getUuid())
				.withSource(goodsDescJson)
				.build());
	}

	/*
	 * 笔记本电脑 
	 * 笔记
	 * biji
	 * bj
	 * 
	 * (non-Javadoc)
	 * @see com.es.spring.publics.search.PublicSearch#getKeyWord(java.lang.String)
	 */
	@Override
	public List<GoodsDescModel> getKeyWord(String keyWord) {
		// 输入热搜词，进行title、spilt  -> mutimatchquery
		NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder()
				.withQuery(QueryBuilders.multiMatchQuery(keyWord, "goodsTitle","goodsSpilt"))
				.withIndices(INDEX_NAME)
				.withTypes(TYPE_NAME)
				.withPageable(new PageRequest(0, 10));
		
		SearchQuery query =builder.build();
		List<GoodsDescModel> descs = et.queryForList(query, GoodsDescModel.class);
		
		return descs;
	}

	@Override
	public List<GoodsModel> getByKeyWord(String keyWord) {
		// 根据联想词 -> 获取到联想词对象
			
			
		List<GoodsDescModel> descs = getKeyWord(keyWord);
		GoodsDescModel gm = null;
		if(descs.size()>0){
			// 1、如果能匹配上，并且比较多，那就按评分排名
			gm = descs.get(0);
		}else{
			// 2、如果匹配不上，通知匹配不上，并且给一些相关推荐
		}
		// 根据唯一一个联想词对象，获取到对方的ID，关联到具体的商品列表 
		
		
		return null;
	}

	
	@Override
	public GoodsDescModel getByKey(int uuid) {
		NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder()
				.withIndices(INDEX_NAME)
				.withTypes(TYPE_NAME)
				.withQuery(QueryBuilders.termQuery("uuid", uuid))
				.withPageable(new PageRequest(0, 10));
		
		SearchQuery query =builder.build();
		
		List<GoodsDescModel> desc = et.queryForList(query, GoodsDescModel.class);
		
		return desc.get(0);
	}
	
	
	public static void main(String[] args) {
		ApplicationContext atc = new ClassPathXmlApplicationContext("applicationContext-es.xml");
		PublicSearchImpl impl = (PublicSearchImpl)atc.getBean("publicSearchImpl");
		// 模拟商品专员，对商品进行添加
//		for(int i=1;i<20;i++){
//			List<String> descs = new ArrayList<String>();
//			descs.add("电脑 "+i);
//			descs.add("速度快 "+i);
//			GoodsDescModel gdm = new GoodsDescModel();
//			gdm.setUuid(i);
//			gdm.setGoodsTitle("笔记本电脑 "+i);
//			gdm.setGoodsSpilt("bjb bjbdn "+i);
//			gdm.setGoodsDesc(descs);
//			
//			impl.addKeyWord(gdm);
//		}
		
//		String keyword = "bjb";
		// 获取前台给的字符串 
//		List<GoodsDescModel> keywordModels = impl.getKeyWord(keyword);
//		for(GoodsDescModel keywordModel : keywordModels){
//			System.out.println("keywordModel="+keywordModel);
//		}
		
		// 根据主键查询keyword
//		GoodsDescModel desc = impl.getByKey(2);
//		System.out.println("desc="+desc);
		
		// 根据热搜词获取商品列表
		
		
	}

}
