package com.lzhsite.technology.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {  
    private static ThreadLocal<SimpleDateFormat> local = new ThreadLocal<SimpleDateFormat>();  
  
    public static Date parse(String str) throws Exception {  
        SimpleDateFormat sdf = local.get();  
        if (sdf == null) {  
            sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);  
            local.set(sdf);  
        }  
        return sdf.parse(str);  
    }  
      
    public static String format(Date date) throws Exception {  
        SimpleDateFormat sdf = local.get();  
        if (sdf == null) {  
            sdf = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);  
            local.set(sdf);  
        }  
        return sdf.format(date);  
    }  
}  