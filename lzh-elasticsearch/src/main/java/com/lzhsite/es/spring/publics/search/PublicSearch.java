package com.lzhsite.es.spring.publics.search;

import java.util.List;

/*
 * 通用搜索演示
 */
public interface PublicSearch {

	// 获取热搜词|联想词的方法
	public void addKeyWord(GoodsDescModel gm);
	
	// 通过点击事件，实时获取相关推荐
	public List<GoodsDescModel> getKeyWord(String keyWord);
	
	// 通过keyword，来获取
	public List<GoodsModel> getByKeyWord(String keyWord);
	
	// 通过uuid-热搜词的key 来获取
	public GoodsDescModel getByKey(int uuid);
	
}
