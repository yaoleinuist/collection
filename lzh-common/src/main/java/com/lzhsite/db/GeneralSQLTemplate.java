package com.lzhsite.db;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lzhsite.core.exception.XSystemException;

public class GeneralSQLTemplate {

	  private static final Logger logger = LoggerFactory.getLogger(GeneralSQLTemplate.class);
	    private static final String IDNAME = "id";

	    public GeneralSQLTemplate() {
	    }

	    public String getLogically(Map<String, Object> para) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.SELECT("*");
	        SqlBuilder.FROM(getTableName((Class)para.get("base")));
	        SqlBuilder.WHERE("id=#{id} and `status` = 1");
	        return SqlBuilder.SQL();
	    }

	    public String getLogicallyBatch(Map<String, Object> para) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.SELECT("*");
	        SqlBuilder.FROM(getTableName((Class)para.get("base")));
	        SqlBuilder.WHERE("id in(" + para.get("ids") + ") and `status` = 1");
	        return SqlBuilder.SQL();
	    }

	    public String getPhysically(Map<String, Object> para) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.SELECT("*");
	        SqlBuilder.FROM(getTableName((Class)para.get("base")));
	        SqlBuilder.WHERE("id=#{id}");
	        return SqlBuilder.SQL();
	    }

	    public String getPhysicallyBatch(Map<String, Object> para) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.SELECT("*");
	        SqlBuilder.FROM(getTableName((Class)para.get("base")));
	        SqlBuilder.WHERE("id in(" + para.get("ids") + ")");
	        return SqlBuilder.SQL();
	    }

	    public String insert(Base obj) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.INSERT_INTO(getTableName(obj.getClass()));
	        obj.caculationColumnList();
	        SqlBuilder.VALUES(obj.returnInsertColumnsName(), obj.returnInsertColumnsDefine());
	        return SqlBuilder.SQL();
	    }

	    public String insertBatch(Map map) {
	        List objs = (List)map.get("list");
	        SqlBuilder.BEGIN();
	        Base tmp = (Base)objs.get(0);
	        SqlBuilder.INSERT_INTO(getTableName(tmp.getClass()));
	        tmp.caculationColumnList();
	        StringBuilder sb = new StringBuilder();
	        sb.append("INSERT INTO ");
	        sb.append(getTableName(tmp.getClass()));
	        sb.append(' ');
	        sb.append('(');
	        sb.append(tmp.returnInsertColumnsName());
	        sb.append(')');
	        sb.append(" VALUES ");
	        String t = tmp.returnInsertColumnsDefine();
	        t = t.replace("{", "\'{\'list[{0}].");
	        MessageFormat mf = new MessageFormat("(" + t + ")");

	        for(int i = 0; i < objs.size(); ++i) {
	            sb.append(mf.format(new Object[]{Integer.valueOf(i)}));
	            if(i < objs.size() - 1) {
	                sb.append(',');
	            }
	        }

	        return sb.toString();
	    }

	    public String update(Base obj) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.UPDATE(getTableName(obj.getClass()));
	        obj.caculationColumnList();
	        SqlBuilder.SET(obj.returnUpdateSet());
	        SqlBuilder.WHERE("id=#{id}");
	        return SqlBuilder.SQL();
	    }

	    public String deletePhysically(Map<String, Object> para) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.DELETE_FROM(getTableName((Class)para.get("base")));
	        SqlBuilder.WHERE("id=#{id}");
	        return SqlBuilder.SQL();
	    }

	    public String deletePhysicallyBatch(Map<String, Object> para) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.DELETE_FROM(getTableName((Class)para.get("base")));
	        SqlBuilder.WHERE("id in(" + para.get("ids") + ") and `status` = 1");
	        return SqlBuilder.SQL();
	    }

	    public String deleteLogically(Map<String, Object> para) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.UPDATE(getTableName((Class)para.get("base")));
	        SqlBuilder.SET("status=0");
	        SqlBuilder.WHERE("id=#{id}");
	        return SqlBuilder.SQL();
	    }

	    public String deleteLogicallyBatch(Map<String, Object> para) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.UPDATE(getTableName((Class)para.get("base")));
	        SqlBuilder.SET("status=0");
	        SqlBuilder.WHERE("id in(" + para.get("ids") + ") and `status` = 1");
	        return SqlBuilder.SQL();
	    }

	    public String getByProperty(Map<String, Object> para) {
	        Class clz = (Class)para.get("base");
	        String propertyName = (String)para.get("propertyName");
	        Object value = para.get("value");

	        Field field;
	        try {
	            field = clz.getDeclaredField(propertyName);
	        } catch (SecurityException | NoSuchFieldException var7) {
	            throw new XSystemException("无法在PO中找到\'" + propertyName + "\'属性", var7);
	        }

	        String columnName = "";
	        if(field.isAnnotationPresent(Column.class)) {
	            columnName = ((Column)field.getAnnotation(Column.class)).name();
	            return "select * from " + getTableName(clz) + " where " + columnName + "=\'" + value + "\' and `status` = 1 limit 1";
	        } else {
	            throw new XSystemException("PO中所查询的属性未设置@Column注解", (Throwable)null);
	        }
	    }

	    public String getListByProperty(Map<String, Object> para) {
	        Class clz = (Class)para.get("base");
	        String propertyName = (String)para.get("propertyName");
	        Object value = para.get("value");

	        Field field;
	        try {
	            field = clz.getDeclaredField(propertyName);
	        } catch (SecurityException | NoSuchFieldException var7) {
	            throw new XSystemException("无法在PO中找到\'" + propertyName + "\'属性", var7);
	        }

	        String columnName = "";
	        if(field.isAnnotationPresent(Column.class)) {
	            columnName = ((Column)field.getAnnotation(Column.class)).name();
	            return "select * from " + getTableName(clz) + " where " + columnName + "=\'" + value + "\' and `status` = 1";
	        } else {
	            throw new XSystemException("PO中所查询的属性未设置@Column注解", (Throwable)null);
	        }
	    }

	    public String getListByPropertiesAnd(Map<String, Object> para) {
	        Class clz = (Class)para.get("base");
	        Property[] properties = (Property[])((Property[])para.get("properties"));
	        String sql = StringUtils.join(new String[]{"select * from ", getTableName(clz), " where "});
	        Property[] var5 = properties;
	        int var6 = properties.length;

	        for(int var7 = 0; var7 < var6; ++var7) {
	            Property property = var5[var7];

	            Field field;
	            try {
	                field = clz.getDeclaredField(property.getName());
	            } catch (SecurityException | NoSuchFieldException var11) {
	                throw new XSystemException("无法在PO中找到\'" + property.getName() + "\'属性", var11);
	            }

	            if(!field.isAnnotationPresent(Column.class)) {
	                throw new XSystemException("PO中所查询的属性未设置@Column注解", (Throwable)null);
	            }

	            String columnName = ((Column)field.getAnnotation(Column.class)).name();
	            sql = StringUtils.join(new String[]{sql, columnName + "=\'" + property.getValue() + "\' and "});
	        }

	        return StringUtils.join(new String[]{sql, " `status` = 1"});
	    }

	    public String getListByPropertiesOr(Map<String, Object> para) {
	        Class clz = (Class)para.get("base");
	        Property[] properties = (Property[])((Property[])para.get("properties"));
	        String sql = StringUtils.join(new String[]{"select * from ", getTableName(clz), " where "});
	        if(properties != null) {
	            Property[] var5 = properties;
	            int var6 = properties.length;

	            for(int var7 = 0; var7 < var6; ++var7) {
	                Property property = var5[var7];

	                Field field;
	                try {
	                    field = clz.getDeclaredField(property.getName());
	                } catch (SecurityException | NoSuchFieldException var11) {
	                    throw new XSystemException("无法在PO中找到\'" + property.getName() + "\'属性", var11);
	                }

	                if(!field.isAnnotationPresent(Column.class)) {
	                    throw new XSystemException("PO中所查询的属性未设置@Column注解", (Throwable)null);
	                }

	                String columnName = ((Column)field.getAnnotation(Column.class)).name();
	                sql = StringUtils.join(new String[]{sql, columnName + "=\'" + property.getValue() + "\' or "});
	            }
	        }

	        if(properties != null && properties.length > 0) {
	            sql = StringUtils.removeEnd(sql, "or ");
	        }

	        return StringUtils.join(new String[]{sql, "and `status` = 1"});
	    }

	    public String getListByProperties(Map<String, Object> para) {
	        Class clz = (Class)para.get("base");
	        Property[] properties = (Property[])((Property[])para.get("properties"));
	        String sql = StringUtils.join(new String[]{"select * from ", getTableName(clz), " where `status` = 1 and "});
	        if(properties != null) {
	            for(int i = 0; i < properties.length; ++i) {
	                Field field;
	                try {
	                    field = clz.getDeclaredField(properties[i].getName());
	                } catch (SecurityException | NoSuchFieldException var8) {
	                    throw new XSystemException("无法在PO中找到\'" + properties[i].getName() + "\'属性", var8);
	                }

	                if(!field.isAnnotationPresent(Column.class)) {
	                    throw new XSystemException("PO中所查询的属性未设置@Column注解", (Throwable)null);
	                }

	                String columnName = ((Column)field.getAnnotation(Column.class)).name();
	                if(i == 0) {
	                    sql = StringUtils.join(new String[]{sql, "( ", columnName, "=\'", properties[i].getValue(), "\' "});
	                } else if(StringUtils.equals(properties[i].getOperator(), "and")) {
	                    sql = StringUtils.join(new String[]{sql, "and ", columnName, "=\'", properties[i].getValue(), "\' "});
	                } else {
	                    sql = StringUtils.join(new String[]{sql, "or ", columnName, "=\'", properties[i].getValue(), "\' "});
	                }
	            }
	        }

	        if(properties != null && properties.length > 0) {
	            sql = StringUtils.join(new String[]{sql, ")"});
	        } else {
	            sql = StringUtils.removeEnd(sql, "and ");
	        }

	        return sql;
	    }

	    public String getListAll(Class clz) {
	        SqlBuilder.BEGIN();
	        SqlBuilder.SELECT("*");
	        SqlBuilder.FROM(getTableName(clz));
	        return SqlBuilder.SQL();
	    }

	    public String getCount(Class clz) {
	        return "select count(*) from " + getTableName(clz);
	    }

	    private static String getTableName(Class clz) {
	        try {
	            if(clz.isAnnotationPresent(Table.class)) {
	                return ((Table)clz.getAnnotation(Table.class)).name();
	            } else {
	                throw new XSystemException("PO中所查询的属性未设置@Column注解", (Throwable)null);
	            }
	        } catch (Exception var2) {
	            logger.error("PO解析异常", var2);
	            throw new XSystemException("PO中缺少@Table注解", (Throwable)null);
	        }
	    }
}