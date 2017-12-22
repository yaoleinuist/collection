package com.lzhsite.core.es;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.hibernate.validator.constraints.NotEmpty;

import com.lzhsite.core.exception.XExceptionFactory;
import com.lzhsite.core.utils.ValidatorUtils;

/**
 * 查询条件构造器
 * Created by ylc on 2017/5/3.
 */
public class EsSearchParam implements Serializable {

    private static final long serialVersionUID = -3615087485300118707L;

    @NotEmpty(message = "F_CORE_ES_1003")
    private String[] indices;

    private String[] types;

    private String[] fields;

    private List<FieldSortBuilder> sortBuilder;

    private BoolQueryBuilder queryBuilder;

    private Integer start = 0;

    private Integer size = 10;

    /**
     * 是否同步获取,默认都是异步超时获取
     */
    private boolean sync = false;

    private int timeoutSeconds = 0;

    public EsSearchParam() {
        queryBuilder = QueryBuilders.boolQuery();
        sortBuilder = new ArrayList<>();
    }

    public EsSearchParam setSync(boolean sync) {
        this.sync = sync;
        return this;
    }

    public boolean isSync() {
        return sync;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public EsSearchParam setTimeoutSeconds(int timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
        return this;
    }

    /**
     * 设置索引相当于数据库中的"数据库"，不设置搜索所有，设置则不能为空
     *
     * @param indices
     * @return
     */
    public EsSearchParam setIndices(String... indices) {
        if (indices == null) {
            throw new ElasticsearchException("indices must not be null");
        }
        this.indices = indices;
        return this;
    }

    /**
     * 设置类型，相当于数据库的表。
     *
     * @param types
     * @return
     */
    public EsSearchParam setTypes(String... types) {
        this.types = types;
        return this;
    }

    /**
     * 设置查询字段
     *
     * @param fields 列表字段列表
     * @return
     */
    public EsSearchParam setFields(String... fields) {
        if (fields == null || fields.length == 0) {
            return this;
        }
        this.fields = fields;
        return this;
    }


    /**
     * 增加排序字段
     *
     * @param field     字段名
     * @param orderType 排序类型 asc,desc
     * @return
     */
    public EsSearchParam addSort(String field, String orderType) {
        if (StringUtils.isNotBlank(field)) {
            SortOrder sortOrder = SortOrder.ASC;
            if (SortOrder.DESC.name().equalsIgnoreCase(orderType)) {
                sortOrder = SortOrder.DESC;
            }
            FieldSortBuilder sortBuilder = new FieldSortBuilder(field).order(sortOrder);
            this.sortBuilder.add(sortBuilder);
        }

        return this;
    }

    /**
     * 增加查询条件
     *
     * @param value           查询内容
     * @param fuzzy           是否模糊匹配
     * @param boost           质量度
     * @param matchingPattern 查询模式
     * @return
     */
    public EsSearchParam addQuery(String value, Boolean fuzzy, Float boost, MatchingPattern matchingPattern) {
        return addQuery("", value, fuzzy, boost, matchingPattern);
    }

    /**
     * 增加查询条件
     *
     * @param value 查询内容
     * @param fuzzy 是否模糊匹配
     * @return
     */
    public EsSearchParam addQuery(String value, Boolean fuzzy) {
        return this.addQuery(value, fuzzy, null, MatchingPattern.MUST);
    }


    /**
     * 增加查询条件
     *
     * @param field           查询字段
     * @param value           查询内容
     * @param fuzzy           是否模糊匹配
     * @param boost           质量度
     * @param matchingPattern 查询模式
     * @return
     */
    public EsSearchParam addQuery(String field, String value, Boolean fuzzy, Float boost, MatchingPattern matchingPattern) {
        if (StringUtils.isBlank(value)) {
            return this;
        }
        if (fuzzy) {
            value = "*" + value + "*";
        }
        if (StringUtils.isNotBlank(field)) {
            WildcardQueryBuilder builder = QueryBuilders.wildcardQuery(field, value);
            if (boost != null) {
                builder.boost(boost);
            }
            addQueryBuilder(builder, matchingPattern);
        } else {
            QueryStringQueryBuilder builder = QueryBuilders.queryStringQuery(value);
            if (boost != null) {
                builder.boost(boost);
            }
            addQueryBuilder(builder, matchingPattern);
        }
        return this;
    }

    /**
     * 增加查询条件
     *
     * @param field 查询字段
     * @param value 查询内容
     * @param fuzzy 是否模糊匹配
     * @return
     */
    public EsSearchParam addQuery(String field, String value, Boolean fuzzy) {
        return addQuery(field, value, fuzzy, null, MatchingPattern.MUST);
    }

    /**
     * 增加查询条件
     *
     * @param fields          查询字段
     * @param value           查询内容
     * @param fuzzy           是否模糊匹配
     * @param boost           质量度
     * @param matchingPattern 查询模式
     * @return
     */
    public EsSearchParam addQuery(String[] fields, String value, Boolean fuzzy, Float boost, MatchingPattern matchingPattern) {
        if (!checkValueEmpty(fields, value)) {
            return this;
        }
        for (String field : fields) {
            addQuery(field, value, fuzzy, boost, matchingPattern);
        }
        return this;
    }

    /**
     * 增加查询条件
     *
     * @param fields 查询字段
     * @param value  查询内容
     * @param fuzzy  是否模糊匹配
     * @return
     */
    public EsSearchParam addQuery(String[] fields, String value, Boolean fuzzy) {
        return addQuery(fields, value, fuzzy, null, MatchingPattern.MUST);
    }

    /**
     * 匹配查询
     *
     * @param field           查询字段
     * @param object          查询内容
     * @param boost           质量度
     * @param matchingPattern 查询模式
     * @return
     */
    public EsSearchParam addQuery(String field, Object object, Float boost, MatchingPattern matchingPattern) {
        if (object == null) {
            return this;
        }
        MatchQueryBuilder builder = QueryBuilders.matchQuery(field, object);
        if (boost != null) {
            builder.boost(boost);
        }
        addQueryBuilder(builder, matchingPattern);
        return this;
    }

    /**
     * 匹配查询
     *
     * @param field  查询字段
     * @param object 查询内容
     * @return
     */
    public EsSearchParam addQuery(String field, Object object) {
        return addQuery(field, object, null, MatchingPattern.MUST);
    }


    /**
     * 增加范围查询
     *
     * @param field
     * @param from
     * @param to
     * @param boost
     * @param matchingPattern
     * @return
     */
    public EsSearchParam addRangeQuery(String field, Object from, Object to, Float boost, MatchingPattern matchingPattern) {
        if (StringUtils.isBlank(field) || (from == null && to == null)) {
            return this;
        }
        RangeQueryBuilder builder = QueryBuilders.rangeQuery(field);
        if (from != null && !"".equals(from)) {
            builder.gte(from);
        }
        if (to != null && !"".equals(to)) {
            builder.lte(to);
        }
        if (boost != null) {
            builder.boost(boost);
        }
        addQueryBuilder(builder, matchingPattern);
        return this;
    }

    /**
     * 增加范围查询
     *
     * @param field
     * @param from
     * @param to
     * @return
     */
    public EsSearchParam addRangeQuery(String field, Object from, Object to) {
        return addRangeQuery(field, from, to, null, MatchingPattern.MUST);
    }

    private void addQueryBuilder(QueryBuilder builder, MatchingPattern matchingPattern) {
        if (matchingPattern == MatchingPattern.SHOULD) {
            this.queryBuilder.should(builder);
        } else if (matchingPattern == MatchingPattern.MUSTNOT) {
            this.queryBuilder.mustNot(builder);
        } else if (matchingPattern == MatchingPattern.MUST) {
            this.queryBuilder.must(builder);
        }
    }

    private Boolean checkValueEmpty(String[] fields, String value) {
        if (fields == null || fields.length < 1 || StringUtils.isBlank(value)) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    private static String filterEscape(String s) {
        //https://www.elastic.co/guide/en/elasticsearch/reference/current/query-dsl-regexp-query.html#query-dsl-regexp-query
        //# @ & < >  ~ these characters may also be reserved
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c != 35 && c != 64 && c != 38 && c != 60 && c != 62 && c != 126) {
                sb.append(c);
            }
        }
        return org.apache.lucene.queryparser.classic.QueryParser.escape(sb.toString());
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String[] getIndices() {
        return indices;
    }

    public String[] getTypes() {
        return types;
    }

    public String[] getFields() {
        return fields;
    }

    public List<FieldSortBuilder> getSortBuilder() {
        return sortBuilder;
    }

    public BoolQueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getSize() {
        return size;
    }

    public enum MatchingPattern {
        MUST("MUST"),
        SHOULD("SHOULD"),
        MUSTNOT("MUSTNOT");

        MatchingPattern(String value) {
            this.value = value;
        }

        private String value;
    }

    public void checkparam() {
        String code;
        if ((code = ValidatorUtils.validateParamsProperty(this)) != null) {
            throw XExceptionFactory.create(code);
        }
    }
}
