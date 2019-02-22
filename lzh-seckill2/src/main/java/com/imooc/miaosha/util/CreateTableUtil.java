package com.imooc.miaosha.util;

import com.imooc.miaosha.domain.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class CreateTableUtil {
	 

	/***
	 * 通过java Class 创建 建表语句
	 */
	    public static Map<String, String> javaProperty2SqlColumnMap = new HashMap<>();
	 
	    static {
	        javaProperty2SqlColumnMap.put("Integer", "INTEGER");
	        javaProperty2SqlColumnMap.put("Short", "tinyint");
	        javaProperty2SqlColumnMap.put("Long", "bigint");
	        javaProperty2SqlColumnMap.put("BigDecimal", "decimal(19,2)");
	        javaProperty2SqlColumnMap.put("Double", "double precision not null");
	        javaProperty2SqlColumnMap.put("Float", "float");
	        javaProperty2SqlColumnMap.put("Boolean", "bit");
	        javaProperty2SqlColumnMap.put("Timestamp", "datetime");
	        javaProperty2SqlColumnMap.put("String", "VARCHAR(255)");
	    }
	 
	    /**
	     * @param args
	     * @throws IOException
	     */
	    public static void main(String[] args) throws IOException {
	         createTable(Goods.class, null);
             createTable(MiaoshaGoods.class, null);
             createTable(MiaoshaOrder.class, null);
             createTable(MiaoshaUser.class, null);
             createTable(OrderInfo.class, null);
            createTable(User.class, null);

	    }
	 
	    public static String createTable(Class obj, String tableName) throws IOException {
	 
	 
	        Field[] fields = null;
	        fields = obj.getDeclaredFields();
//	        Class annotationType = null;
	        String param = null;
	        String column = null;
//	        XmlElement xmlElement = null;
	        StringBuilder sb = null;
	        sb = new StringBuilder(50);
	        if (tableName == null || tableName.equals("")) {
	//未传表明默认用类名
	            tableName = obj.getName();
	            tableName = tableName.substring(tableName.lastIndexOf(".") + 1);
	        }
//	        sb.append("\r\ndrop table if exists  ").append(tableName).append(";\r\n");
	        sb.append("create table ").append(tableName).append(" ( \r\n");
	        System.out.println(tableName);
	        boolean firstId = true;
	        File file = null;
	        for (Field f : fields) {
	            column = f.getName();
	            if (column.equals("serialVersionUID")) {
	                continue;
	            }
//	            sb.append(column).append(" ");
//	            System.out.println(column + "," + f.getType());
	            param = f.getType().getSimpleName();
	            sb.append(column);//一般第一个是主键
	            sb.append(" ").append(javaProperty2SqlColumnMap.get(param)).append(" ");
	            /*if (param instanceof Integer) {
	                sb.append(" INTEGER ");
	            } else if (param instanceof Short) {
	                sb.append(" tinyint ");
	            } else if (param instanceof Long) {
	                sb.append(" bigint ");
	            } else if (param instanceof BigDecimal) {
	                sb.append(" decimal(19,2) ");
	            } else if (param instanceof Double) {
	                sb.append(" double precision not null ");
	            } else if (param instanceof Float) {
	                sb.append(" float ");
	            } else if (param instanceof Boolean) {
	                sb.append(" bit ");
	            } else if (param instanceof Timestamp) {
	                sb.append(" datetime ");
	            } else {
	                sb.append("  ");//根据需要自行修改
	            }*/
	            if (firstId) {//类型转换
	                sb.append(" PRIMARY KEY ");
	                firstId = false;
	            }
	            //获取字段中包含fieldMeta的注解
	            //2、获取属性上的所有注释
	            Annotation[] allAnnotations = f.getAnnotations();
	            /*for(Annotation an : allAnnotations){
	                sb.append(" COMMIT '");
	                xmlElement = (XmlElement)an;
	                annotationType = an.annotationType();
	                param = ((XmlElement) an).name();
	                System.out.println("属性【"+f.getName()+"-----的注释类型有: " + param);
	                sb.append(param).append("'");
	            }*/
	            sb.append(",\n ");
	        }
	        String sql = null;
	        sql = sb.toString();
	        //去掉最后一个逗号
	        int lastIndex = sql.lastIndexOf(",");
	        sql = sql.substring(0, lastIndex) + sql.substring(lastIndex + 1);
	 
	        sql = sql.substring(0, sql.length() - 1) + " )ENGINE =INNODB DEFAULT  CHARSET= utf8;\r\n";
	        System.out.println("sql :" + sql);
	 
	        //复制到剪切板
	        SysClipboardUtil.setSysClipboardText(sql);
//	        ToastMessage.toast("复制到剪切板",2000, Color.blue);
	        return sql;
	        /*file = new File("WebContent/createTable/建表.txt");
	        if (!file.getParentFile().exists()) {
	            if (!file.getParentFile().mkdirs()) {
	            }
	        }
	        if (!file.exists()) {
	            file.createNewFile();
	        }
	        System.out.println("文件路径:"+file.getAbsolutePath());
	        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
	        out.write(sql) ;
	        out.flush();
	        out.close() ;*/
	 
	    }

}
