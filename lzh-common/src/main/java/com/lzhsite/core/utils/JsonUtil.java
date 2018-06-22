package com.lzhsite.core.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtil {

    private static Gson gson = null;

    public JsonUtil() {
    }

    public static synchronized Gson newInstance() {
        if(gson == null) {
            gson = new Gson();
        }

        return gson;
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T toBean(String json, Class<T> clz) {
        return gson.fromJson(json, clz);
    }

    public static <T> Map<String, T> toMap(String json, Class<T> clz) {
        Map map = (Map)gson.fromJson(json, (new TypeToken() {
        }).getType());
        HashMap result = new HashMap();
        Iterator var4 = map.keySet().iterator();

        while(var4.hasNext()) {
            String key = (String)var4.next();
            result.put(key, gson.fromJson((JsonElement)map.get(key), clz));
        }

        return result;
    }

    public static Map<String, Object> toMap(String json) {
        Map map = (Map)gson.fromJson(json, (new TypeToken() {
        }).getType());
        return map;
    }

    public static <T> List<T> toList(String json, Class<T> clz) {
        JsonArray array = (new JsonParser()).parse(json).getAsJsonArray();
        ArrayList list = new ArrayList();
        Iterator var4 = array.iterator();

        while(var4.hasNext()) {
            JsonElement elem = (JsonElement)var4.next();
            list.add(gson.fromJson(elem, clz));
        }

        return list;
    }

    public static void main(String[] args) {
    }

    static {
        gson = new Gson();
    }
}
