package com.lzhsite.es.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import com.lzhsite.exception.XExceptionFactory;
import com.lzhsite.util.CollectionUtils;
import com.lzhsite.util.ValidatorUtils;

/**
 * es操作参数类
 *
 * @author guoqw
 * @since 2017-03-30 15:11
 */
public final class EsOperateParam implements Serializable {

    private static final long serialVersionUID = 2532509826091658212L;

    // 只能通过builder类创建
    private EsOperateParam() {
    }

    public static final EsOperateParam EMPTY = new EsOperateParam();

    /**
     * 文档id,即实体id
     */
    @NotEmpty(message = "F_CORE_ES_1001", groups = {
            Save.class,
            Delete.class,
            Update.class,
            QueryById.class
    })
    private String id;

    /**
     * 文档对象，即实体
     */
    @NotNull(message = "F_CORE_ES_1002", groups = {
            Save.class
    })
    private Object document;

    @NotEmpty(message = "F_CORE_ES_1003", groups = {
            Save.class,
            Delete.class,
            Update.class,
            Query.class,
            QueryById.class
    })
    private String indexName;

    @NotEmpty(message = "F_CORE_ES_1004", groups = {
            Save.class,
            Delete.class,
            Update.class,
            Query.class,
            QueryById.class
    })
    private String typeName;

    /**
     * 回调函数
     */
    private EsOperateCallback callback;

    /**
     * 超时时间,此次调用的超时时间.
     * 优先级大于默认的超时时间，默认为0时将使用默认的超时时间
     */
    @Min(value = 0, message = "F_CORE_ES_1005", groups = {
            Save.class,
            Delete.class,
            Update.class,
            Query.class,
            QueryById.class
    })
    private int timeoutSeconds = 0;

    /**
     * 是否同步获取,默认都是异步超时获取
     */
    private boolean sync = false;

    /**
     * 实体属性、值集合,通常用于更新多个字段
     */
/*    @NotEmpty(message = "F_CORE_ES_1006", groups = {
            Update.class
    })*/
    private Map<String, Object> fieldValues = new HashMap<>();

    /**
     * 是否模糊匹配
     */
    private boolean fuzzy = false;

    /**
     * 查询的属性名称
     */
    @NotEmpty(message = "F_CORE_ES_1007", groups = {
            Query.class
    })
    private String searchField;

    /**
     * 查询的属性值
     */
    @NotNull(message = "F_CORE_ES_1008", groups = {
            Query.class
    })
    private Object searchValue;

    /**
     * 从第几页开始查询
     */
    private int from = 0;

    /**
     * 查询数量
     */
    private int size = 10;

    public static SaveBuilder saveBuilder() {
        return new SaveBuilder();
    }

    public static DeleteBuilder deleteBuilder() {
        return new DeleteBuilder();
    }

    public static UpdateBuilder updateBuilder() {
        return new UpdateBuilder();
    }

    public static QueryBuilder queryBuilder() {
        return new QueryBuilder();
    }

    // 检查用于保存的入参属性配置
    public void checkSave() {
        checkparam(Save.class);
    }

    // 检查用于删除的入参属性配置
    public void checkDelete() {
        checkparam(Delete.class);
    }

    // 检查用于更新的入参属性配置
    public void checkUpdate() {
        // document obj和fieldValues2者至少其一不为空，有限使用document对象
        if (this.document == null && CollectionUtils.isEmpty(fieldValues)) {
            throw XExceptionFactory.create("F_CORE_ES_1006");
        }
        checkparam(Update.class);
    }

    // 检查用于查询的入参属性配置
    public void checkQueryById(Class documentClass) {
        checkparam(QueryById.class);
        checkDocumentClass(documentClass);
    }

    // 检查用于查询的入参属性配置
    public void checkQuery(Class documentClass) {
        checkparam(Query.class);
        checkDocumentClass(documentClass);
    }

    private void checkDocumentClass(Class documentClass) {
        if (documentClass == null) {
            throw XExceptionFactory.create("F_CORE_ES_1009");
        }
    }

    private void checkparam(Class groupClass) {
        String code;
        if ((code = ValidatorUtils.validateParamsProperty(this, groupClass)) != null) {
            throw XExceptionFactory.create(code);
        }
    }

    public static class SaveBuilder {

        private EsOperateParam param = new EsOperateParam();

        public SaveBuilder id(Long id) {
            this.param.id = id == null ? null : id.toString();
            return this;
        }

        public SaveBuilder id(String id) {
            this.param.id = id;
            return this;
        }

        public SaveBuilder indexName(String indexName) {
            this.param.indexName = indexName;
            return this;
        }

        public SaveBuilder typeName(String typeName) {
            this.param.typeName = typeName;
            return this;
        }

        public SaveBuilder timeoutSeconds(int timeoutSeconds) {
            this.param.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public SaveBuilder sync(boolean sync) {
            this.param.sync = sync;
            return this;
        }

        public SaveBuilder document(Object document) {
            this.param.document = document;
            return this;
        }

        public SaveBuilder callback(EsOperateCallback callback) {
            this.param.callback = callback;
            return this;
        }

        public EsOperateParam build() {
            return param;
        }

    }

    public static class DeleteBuilder {
        private EsOperateParam param = new EsOperateParam();

        public DeleteBuilder id(Long id) {
            this.param.id = id == null ? null : id.toString();
            return this;
        }

        public DeleteBuilder id(String id) {
            this.param.id = id;
            return this;
        }

        public DeleteBuilder indexName(String indexName) {
            this.param.indexName = indexName;
            return this;
        }

        public DeleteBuilder typeName(String typeName) {
            this.param.typeName = typeName;
            return this;
        }

        public DeleteBuilder timeoutSeconds(int timeoutSeconds) {
            this.param.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public DeleteBuilder sync(boolean sync) {
            this.param.sync = sync;
            return this;
        }

        public DeleteBuilder callback(EsOperateCallback callback) {
            this.param.callback = callback;
            return this;
        }

        public EsOperateParam build() {
            return param;
        }
    }

    public static class UpdateBuilder {
        private EsOperateParam param = new EsOperateParam();

        public UpdateBuilder id(Long id) {
            this.param.id = id == null ? null : id.toString();
            return this;
        }

        public UpdateBuilder id(String id) {
            this.param.id = id;
            return this;
        }

        public UpdateBuilder indexName(String indexName) {
            this.param.indexName = indexName;
            return this;
        }

        public UpdateBuilder typeName(String typeName) {
            this.param.typeName = typeName;
            return this;
        }

        public UpdateBuilder timeoutSeconds(int timeoutSeconds) {
            this.param.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public UpdateBuilder sync(boolean sync) {
            this.param.sync = sync;
            return this;
        }

        public UpdateBuilder fieldValue(String field, Object value) {
            this.param.fieldValues.put(field, value);
            return this;
        }

        public UpdateBuilder fieldValues(Map<String, Object> fieldValues) {
            this.param.fieldValues.putAll(fieldValues);
            return this;
        }

        public UpdateBuilder document(Object document) {
            this.param.document = document;
            return this;
        }

        public UpdateBuilder callback(EsOperateCallback callback) {
            this.param.callback = callback;
            return this;
        }

        public EsOperateParam build() {
            return param;
        }
    }

    public static class QueryBuilder {
        private EsOperateParam param = new EsOperateParam();

        public QueryBuilder id(Long id) {
            this.param.id = id == null ? null : id.toString();
            return this;
        }

        public QueryBuilder id(String id) {
            this.param.id = id;
            return this;
        }

        public QueryBuilder indexName(String indexName) {
            this.param.indexName = indexName;
            return this;
        }

        public QueryBuilder typeName(String typeName) {
            this.param.typeName = typeName;
            return this;
        }

        public QueryBuilder timeoutSeconds(int timeoutSeconds) {
            this.param.timeoutSeconds = timeoutSeconds;
            return this;
        }

        public QueryBuilder sync(boolean sync) {
            this.param.sync = sync;
            return this;
        }

        public QueryBuilder fuzzy(boolean fuzzy) {
            this.param.fuzzy = fuzzy;
            return this;
        }

        public QueryBuilder fieldValue(String field, Object value) {
            this.param.searchField = field;
            this.param.searchValue = value;
            return this;
        }

        public QueryBuilder from(int from) {
            this.param.from = from;
            return this;
        }

        public QueryBuilder size(int size) {
            this.param.size = size;
            return this;
        }

        public EsOperateParam build() {
            return param;
        }
    }

    public @interface Save {
    }

    public @interface Delete {
    }

    public @interface Update {
    }

    public @interface QueryById {

    }

    public @interface Query {
    }

    public String getId() {
        return id;
    }

    public String getIndexName() {
        return indexName;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getTimeoutSeconds() {
        return timeoutSeconds;
    }

    public boolean isSync() {
        return sync;
    }

    public Object getDocument() {
        return document;
    }

    public Map<String, Object> getFieldValues() {
        return fieldValues;
    }

    public boolean isFuzzy() {
        return fuzzy;
    }

    public String getSearchField() {
        return searchField;
    }

    public Object getSearchValue() {
        return searchValue;
    }

    public int getFrom() {
        return from;
    }

    public int getSize() {
        return size;
    }

    public EsOperateCallback getCallback() {
        return callback;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("document", document)
                .append("indexName", indexName)
                .append("typeName", typeName)
                .append("timeoutSeconds", timeoutSeconds)
                .append("sync", sync)
                .append("fieldValues", fieldValues)
                .append("fuzzy", fuzzy)
                .append("searchField", searchField)
                .append("searchValue", searchValue)
                .append("from", from)
                .append("size", size)
                .toString();
    }
}
