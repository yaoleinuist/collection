package com.lzhsite.core.utils;


import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nt on 2015-06-10.
 */
public class MapUtils {

    private static final String HTTP_DELIMITER = "&";

    public static Map<String, String> convertValuesToString(Map<String, String[]> params){
    	Map<String, String> tempMap = new HashMap<>();

        for(Map.Entry<String,String[]> entry : params.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value != null && value.getClass().isArray()) {
                tempMap.put(key, (String) Array.get(value, 0));
            } else if (value != null) {
                tempMap.put(key, value.toString());
            } else {
                tempMap.put(key, StringUtils.EMPTY);
            }
        }
        return tempMap;
    }
    
    public static Map<String, String> convertValueToString(Map<String, Object> params){
        Map<String, String> tempMap = new HashMap<>();

        for(Map.Entry<String,Object> entry : params.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
            if(value != null && value.getClass().isArray()) {
                tempMap.put(key, (String) Array.get(value, 0));
            } else if (value != null) {
                tempMap.put(key, value.toString());
            } else {
                tempMap.put(key, StringUtils.EMPTY);
            }
        }

        return tempMap;
    }

    public static String convertMapToHttpString(Map<String,String> map) {
        StringBuilder stringBuffer = new StringBuilder();
        for(Map.Entry<String,String> entry : map.entrySet()) {
            String delimiter = (stringBuffer.length()>0) ? HTTP_DELIMITER : StringUtils.EMPTY;
            stringBuffer.append(delimiter).append(entry.getKey()).append('=').append(entry.getValue());
        }
        return stringBuffer.toString();
    }

}
