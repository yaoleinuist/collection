package com.lzhsite.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Zhenwei on 2015/5/22.
 */
public class CollectionUtils {

    public static boolean isEmpty(Collection coll) {
        return coll == null || coll.isEmpty();
    }

    public static boolean isNotEmpty(Collection coll) {
        return !isEmpty(coll);
    }

    /**
     * 已废弃。太容易被误用
     */
    @Deprecated
    public static boolean isEmpty(Object... objs) {
        return objs == null || objs.length <= 0;
    }

    public static <T> T getLast(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }

    public static boolean isEmpty(Map map) {
        return map == null || map.isEmpty();
    }

    public static boolean isNotEmpty(Map map) {
        return !isEmpty(map);
    }

}
