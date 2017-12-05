package com.lzhsite.es.spring.model;

import java.io.Serializable;

import org.elasticsearch.action.fieldstats.FieldStats.Date;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.fasterxml.jackson.annotation.JsonFormat;
/*
    @org.springframework.data.annotation.Id 用于标记elasticsearch mapping里的_id。没有ID是保存不了的。
	@Field 可以对mappings做一些额外的配置，例如指定数据的分词器设置ngram或ik、paoding等，还可以设置index的store等属性，
	具体你可以搜索一下elasticsearch的mappings配置文章，在此不再赘述,
	注意一下如果要实现时间的排序和范围搜索，还要额外指定下@JsonFormat，可以参考例子的代码。
	public @interface Field {

    FieldType type() default FieldType.Auto;

    FieldIndex index() default FieldIndex.analyzed;

    DateFormat format() default DateFormat.none;

    String pattern() default "";

    boolean store() default false;

    String searchAnalyzer() default "";

    String analyzer() default "";

    String[] ignoreFields() default {};

    boolean includeInParent() default false;
}
	
*/
@Document(indexName = "ibeifeng-java", type = "javaapi")
@Setting(settingPath = "elasticsearch-analyser.json")
public class ArticleInfModel implements Serializable {

	// elasticsearch
	@org.springframework.data.annotation.Id
	private Integer articleInfId;

	@Field(type = FieldType.String, analyzer = "ngram_analyzer") // 使用ngram进行单字分词
	private String articleTitle;

	@Field(type = FieldType.Date, store = true, format = DateFormat.custom, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ")
	private Date releaseTime;

	public Date getReleaseTime() {
		return releaseTime;
	}

	public void setReleaseTime(Date releaseTime) {
		this.releaseTime = releaseTime;
	}

	public String getArticleTitle() {
		return articleTitle;
	}

	public void setArticleTitle(String articleTitle) {
		this.articleTitle = articleTitle;
	}

}
