
package com.lzhsite.util;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
    public static final String FORMAT_SHORT = "yyyy-MM-dd";
    public static final String FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_FULL = "yyyy-MM-dd HH:mm:ss.S";
    public static final String FORMAT_SHORT_CN = "yyyy年MM月dd日";
    public static final String FORMAT_LONG_CN = "yyyy年MM月dd日  HH时mm分ss秒";
    public static final String FORMAT_FULL_CN = "yyyy年MM月dd日  HH时mm分ss秒SSS毫秒";
    public static final String FORMAT_INT_MINITE = "yyyyMMddHHmmss";
    public static final String FORMAT_INT_DATE = "yyyyMMdd";

    public DateUtils() {
    }

    public static String getDatePattern() {
        return "yyyy-MM-dd HH:mm:ss";
    }

    public static Date getCurrent() {
        Calendar c = Calendar.getInstance();
        return c.getTime();
    }

    public static String getNow() {
        return format(new Date());
    }

    public static String getNow(String format) {
        return format(new Date(), format);
    }

    public static String format(Date date) {
        return format(date, getDatePattern());
    }

    public static String format(Date date, String pattern) {
        String returnValue = "";
        if(date != null) {
            SimpleDateFormat df = new SimpleDateFormat(pattern);
            returnValue = df.format(date);
        }

        return returnValue;
    }

    public static Date parse(String strDate) {
        return parse(strDate, getDatePattern());
    }

    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);

        try {
            return df.parse(strDate);
        } catch (ParseException var4) {
            logger.error("日期格式转换错误", var4);
            return null;
        }
    }

    public static Date addMonth(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(2, n);
        return cal.getTime();
    }

    public static Date addDay(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(5, n);
        return cal.getTime();
    }

    public static Date addHour(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(10, n);
        return cal.getTime();
    }

    public static Date addMinute(Date date, int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(12, n);
        return cal.getTime();
    }

    public static String getTimeString() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
        Calendar calendar = Calendar.getInstance();
        return df.format(calendar.getTime());
    }

    public static String getYear(Date date) {
        return format(date).substring(0, 4);
    }

    public static int countDays(String date) {
        long t = Calendar.getInstance().getTime().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(parse(date));
        long t1 = c.getTime().getTime();
        return (int)(t / 1000L - t1 / 1000L) / 3600 / 24;
    }

    public static int countDays(String date, String format) {
        long t = Calendar.getInstance().getTime().getTime();
        Calendar c = Calendar.getInstance();
        c.setTime(parse(date, format));
        long t1 = c.getTime().getTime();
        return (int)(t / 1000L - t1 / 1000L) / 3600 / 24;
    }

    public static Date getDayBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        return calendar.getTime();
    }

    public static Date getDayEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        date = getDayBegin(date);
        calendar.setTime(date);
        calendar.add(5, 1);
        calendar.add(13, -1);
        return calendar.getTime();
    }

    public static Date getDifferentTime(int day, int hour, int minute, int second) {
        GregorianCalendar calendar = (GregorianCalendar)Calendar.getInstance();
        calendar.add(5, day);
        calendar.add(10, hour);
        calendar.add(12, minute);
        calendar.add(13, second);
        return calendar.getTime();
    }

    public static String fillGap(int num, int figure) {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumIntegerDigits(figure);
        formatter.setGroupingUsed(false);
        return formatter.format((long)num);
    }

    public static int getMinutesBetween(Date beginDate, Date endDate) {
        return null != beginDate && null != endDate?Minutes.minutesBetween(new DateTime(beginDate), new DateTime(endDate)).getMinutes():0;
    }

    public static int getSecondsBetween(Date beginDate, Date endDate) {
        return null != beginDate && null != endDate?Seconds.secondsBetween(new DateTime(beginDate), new DateTime(endDate)).getSeconds():0;
    }

    public static long getMillSecondsBetween(DateTime beginDate, DateTime endDate) {
        return null != beginDate && null != endDate?beginDate.getMillis() - endDate.getMillis():0L;
    }

    public static boolean isSameDay(Date sourceDate, Date targetDate) {
        return Days.daysBetween(new DateTime(sourceDate), new DateTime(targetDate)).getDays() == 0;
    }

    public static boolean isCurrentDay(Date date) {
        return Days.daysBetween(DateTime.now(), new DateTime(date)).getDays() == 0;
    }

    public static long getMillis(Date date) {
        return (new DateTime(date)).getMillis();
    }
}
