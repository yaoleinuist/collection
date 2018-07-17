package com.lzhsite.core.utils;

/**
 * Created by lzh on 2018/7/10
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import com.csvreader.CsvReader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * CSV文件导入导出工具类
 * <p/>
 * Created on 2014-08-07
 *
 * @author
 * @reviewer
 */
public class CSVUtil {
    static Logger log = LoggerFactory.getLogger(CSVUtil.class);
    /**
     * CSV文件列分隔符
     */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /**
     * CSV文件列分隔符
     */
    private static final String CSV_RN = "\r\n";

    /**
     * 将检索数据输出的对应的csv列中
     */
    public static String formatCsvData(List<?> data, String title, String table) {

        StringBuffer buf = new StringBuffer();
        StringBuffer bufTitle = new StringBuffer();
        String[] fields = title.split(",");
        bufTitle.append(table).append(StringUtils.repeat(",", fields.length - 1)).append(CSV_RN);
        bufTitle.append(title).append(CSV_RN);
        int dataCount = data.size();
        int successCount = 0;
        for (Object obj : data)
        {
            Class<?> objClass = obj.getClass();
            try {
                for (int i = 0; i < fields.length; i++)
                {
                    String name = fields[i];
                    String getMethodName = "get" + toFirstLetterUpperCase(name);
                    try {

                        Object value = objClass.getMethod(getMethodName).invoke(obj);
                        if (objClass.getMethod(getMethodName).getReturnType() == Date.class) {
                            value =  com.lzhsite.core.utils.DateUtils.format((Date) value,"yyyy-mm-dd hh:mm:ss");
                        }
                        if (null == value || value == "") {
                            buf.append("");
                            if (i < fields.length - 1) {
                                buf.append(CSV_COLUMN_SEPARATOR);
                            }
                        } else {
                            String v = value.toString().replaceAll("\"", "\"\"");
                            buf.append(v.contains(",") ? String.format("\"%s\"", v) : value.toString());
                            if (i < fields.length - 1) {
                                buf.append(CSV_COLUMN_SEPARATOR);
                            }
                        }

                    } catch (Exception e) {
                        log.error("导出cvs异常:{}",e);
                        return null;
                    }
                }
                buf.append(CSV_RN);
            } catch (Exception e) {
                log.error("导出cvs异常:{}",e);
                return null;
            }
            successCount++;
        }
        if(dataCount<successCount)
        {
            log.error("导出数据不完整");
            return  null;
        }
        bufTitle.append(buf);
        return bufTitle.toString();
    }

    public static void exportCsv(String fileName, String content,
                                 HttpServletResponse response) throws IOException {

        // 设置文件后缀
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhh24mmss");
        String fn = fileName.concat(sdf.format(new Date()).toString() + ".csv");

        // 读取字符编码
        String csvEncoding = "UTF-8";

        // 设置响应
        response.setCharacterEncoding(csvEncoding);
        response.setContentType("text/csv; charset=" + csvEncoding);
        response.setHeader("Pragma", "public");
        response.setHeader("Cache-Control", "max-age=30");
        response.setHeader("Content-Disposition", "attachment; filename="
                + new String(fn.getBytes(), csvEncoding));

        // 写出响应
        OutputStream os = response.getOutputStream();
        os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
        os.write(content.getBytes(csvEncoding));
        os.flush();
        os.close();
    }

    public static List<String[]> readCSVFile(File file) throws Exception {

        final ExecutorService exec = Executors.newFixedThreadPool(1);

        Callable<List<String[]>> call = new Callable<List<String[]>>() {
            public   List<String[]>  call() throws Exception {
                //开始执行耗时操作

                List<String[]> listFile = new ArrayList<>();
                CsvReader csvReader = new CsvReader(FileUtil.openInputStream(file), Charset.forName("GBK"));
                //                List<String> headList = new ArrayList<String>();
                List<String[]> dataList = new ArrayList<String[]>();
                //对读取的行数进行限制计数
                int currentLines = 0;
                while (csvReader.readRecord()) {
                    //行数计数
                    currentLines++;
                    //逐行读入数据
                    String[] str = csvReader.getValues();
                    if (str != null && str.length > 0) {
                        dataList.add(str);
                    }
                }

                return dataList;
            }
        };

        List<String[]>  obj = null;
        try {
            Future<List<String[]>> future = exec.submit(call);
            obj = future.get(5, TimeUnit.SECONDS); //任务处理超时时间设为 5 秒
            log.info("读取文件成功返回:" + obj);

        } catch (Exception e) {
            log.info("读取文件处理失败"+e.getMessage());
            throw  new Exception("读取文件处理失败"+e.getMessage());
        }
        // 关闭线程池
        exec.shutdown();
        return obj;
    }


    /**
     * @param f
     * @param className
     * @param isAdd     是否新增
     * @param title     columns
     * @param table     文件识别
     * @return
     * @throws Exception
     */
    public static List<Object> setObject(File f, String className, boolean isAdd, String title, String table) throws Exception {
        List<Object> listObject = new ArrayList<>();
        List<String[]> listFile = readCSVFile(f);
        Iterator<String[]> it = listFile.iterator();
        String[] t = it.next();
        if (t[0] == null || t[0].isEmpty() || !t[0].contains(table)) {
            return listObject;
        }
        String[] a = it.next();
        StringUtils.stripAll(a);
//    		String[] pro = title.split(",");
//    		System.out.println(a[0].equals(pro[0]));
        while (it.hasNext()) {
            String[] objSt = it.next();
            Class<?> cl = Class.forName(className);
            Object rt = cl.newInstance();//创建一个对象
            Field[] fields = cl.getDeclaredFields();

            Map<String, Field> m = new HashMap<>();
            for (int i = 0; i < fields.length; i++) {
                String name = fields[i].getName();
                m.put(name, fields[i]);
            }
            for (int i = 0; i < a.length; i++) {
                Field field = m.get(a[i]);
                if (null == field || (isAdd && field.getName().equalsIgnoreCase("id"))) {
                    continue;
                }
                field.setAccessible(true);
                String s = objSt[i].replaceAll("\\|", ",");
                s = s.replaceAll("，", ",");
                if (s.isEmpty() || s.contains("null")) {
                    continue;
                }
                if (field.getType() == String.class) {
                    field.set(rt, s);
                } else if (field.getType() == int.class || field.getType() == Integer.class) {
                    field.set(rt, Integer.valueOf(s));
                } else if (field.getType() == double.class || field.getType() == Double.class) {
                    field.set(rt, Double.valueOf(s));
                } else if (field.getType() == BigDecimal.class) {
                    field.set(rt, new BigDecimal(s));
                } else if (field.getType() == long.class || field.getType() == Long.class) {
                    field.set(rt, Long.valueOf(s));
                } else if (field.getType() == short.class || field.getType() == Short.class) {
                    field.set(rt, Short.valueOf(s));
                } else if (field.getType() == Date.class) {
                    if ( com.lzhsite.core.utils.DateUtils.isValidDate(s)) {
                        field.set(rt,  com.lzhsite.core.utils.DateUtils.parse(s));
                    } else {
                        field.set(rt, new Date(s));
                    }
                } else if (field.getType() == char.class) {
                    field.set(rt, s.charAt(0));
                } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                    field.set(rt, Boolean.getBoolean(s));
                } else if (field.getType() == float.class || field.getType() == Float.class) {
                    field.set(rt, Float.valueOf(s));
                } else if (field.getType() == BigDecimal.class) {
                    field.set(rt, new BigDecimal(s));
                }
            }
            listObject.add(rt);
        }

        return listObject;
    }

    public static String toFirstLetterUpperCase(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }
        String firstLetter = str.substring(0, 1).toUpperCase();
        return firstLetter + str.substring(1, str.length());
    }

    /**
     * 获取编码方式
     *
     * @param
     * @return 编码方式
     * @throws Exception
     */
    public static String codeString(File file) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream bin = new BufferedInputStream(fileInputStream);
        int p = (bin.read() << 8) + bin.read();
        String code = null;
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        IOUtils.closeQuietly(bin);
        IOUtils.closeQuietly(fileInputStream);
        return code;
    }

}

