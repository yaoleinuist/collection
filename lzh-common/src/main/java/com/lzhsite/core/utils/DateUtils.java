
package com.lzhsite.core.utils;

import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Minutes;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.joda.time.Seconds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

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
    

    public static final String PATTERN_YYYYMMDD = "yyyyMMdd";

    public static final String PATTERN_YYYY_MM_DD = "yyyy-MM-dd";

    public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
    /**
     * Date format pattern used to parse HTTP date headers in RFC 1123 format.
     */
    public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in RFC 1036 format.
     */
    public static final String PATTERN_RFC1036 = "EEEE, dd-MMM-yy HH:mm:ss zzz";

    /**
     * Date format pattern used to parse HTTP date headers in ANSI C 
     * <code>asctime()</code> format.
     */
    public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
    
    public static final String PATTERN_CHINESE_NORMAL = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_CHINESE_NOSEC = "yyyy-MM-dd HH:mm";
    
    public static SimpleDateFormat yyyyMMddHHmmssFormat = new SimpleDateFormat("yyyyMMddHHmmss");


    @SuppressWarnings("rawtypes")
	private static final Collection DEFAULT_PATTERNS = Arrays.asList(
            new String[] { PATTERN_CHINESE_NORMAL, PATTERN_RFC1036, PATTERN_RFC1123 } );
    
    private static final Date DEFAULT_TWO_DIGIT_YEAR_START;
    
    static {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2000, Calendar.JANUARY, 1, 0, 0);
        DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime(); 
    }
	public static Date getDateFormatter(String dateTime) throws ParseException {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
	}
    
	/**
	 * 根据默认格式将字符串转换成时间对象(java.util.Dates)
	 * 
	 * @param str
	 *            String
	 * @return Date
	 */
	public static Date stringToDate(String str) {
		if (str == null) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_CHINESE_NORMAL);
		try {
			return sdf.parse(str);
		} catch (ParseException ex) {
			return null;
		}
	}
	/**
	 * 根据默认格式将字符串转换成时间对象(java.util.Dates)
	 *
	 * @param date
	 *            String
	 * @return Date
	 */
	public static String dateToString(Date date) {
		return dateToString(date,PATTERN_CHINESE_NORMAL);
	}

	/**
	 * 时间对象(java.util.Dates)转换为指定格式的字符串
	 *
	 * @param date
	 *            String
	 * @return Date
	 */
	public static String dateToString(Date date,String pattern) {
		if (date == null || StringUtils.isEmpty(pattern)) {
			return null;
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.format(date);
		} catch (Exception ex) {
			return null;
		}
	}


	/**
	 * 格式化时间,去掉后面的.0
	 * @param str
	 * @return
     */
	public static String stringDateFormat(String str){

		if (str == null) {
			Format format = new SimpleDateFormat(PATTERN_CHINESE_NORMAL);
			return format.format(new Date());
		}
		SimpleDateFormat sdf = new SimpleDateFormat(PATTERN_CHINESE_NORMAL);
		try {
			Date date = sdf.parse(str);

			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(PATTERN_CHINESE_NORMAL);
			return simpleDateFormat.format(date);
		} catch (ParseException ex) {
			return null;
		}

	}
 


    /**
     * 
     * 计算日期的间隔天数
     * @param begin
     * @param end
     * @return
     */
    public static int getDatePeriod(Date begin,Date end){
    	if (begin == null || end == null) {
			return 0;
		}
    	DateTime beginDateTime = new DateTime(begin.getTime());
    	DateTime endDateTime = new DateTime(end.getTime());
    	Period p = new Period(beginDateTime, endDateTime, PeriodType.days());
    	return p.getDays();  
    }
	

	
	/**
	 * 获取间隔自然月后的日期
	 * @param begin 开始日期
	 * @param intervalMonthNum 间隔月数
	 * @return
	 */
	public static Date getDateByIntervalMonth(Date begin, int intervalMonthNum) {
		if (begin == null) {
			return null;
		}
		DateTime beginDateTime = new DateTime(begin.getTime());
		return beginDateTime.plusMonths(intervalMonthNum).toDate();
	}

    /** This class should not be instantiated. */
    private DateUtils() { }


    
    /**
	 * 增加日期天数
	 * @param day
	 * @return
	 */
	public static String add_days(int day, String... date) {
		String resultDate;
		SimpleDateFormat from_sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();;
		if(date != null && date.length > 0 && date[0]!=null){
			try {
				Date tempDate = from_sdf.parse(date[0]);
				cal.setTime(tempDate);
			} catch (ParseException e) {
			}
		}
		
		cal.add(Calendar.DAY_OF_YEAR, day);
		resultDate = from_sdf.format(cal.getTime());
		return resultDate;
	}

	/**2017-3-2
	 * 增加日期天数
	 * @param day
	 * @return
	 */
	public static Date addDays(Date date,int day) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, day);
		return cal.getTime();
	}
	
	/**
	 * 增加日期分钟
	 * @param min
	 * @return
	 */
	public static String addMin(int min, String date2) {
		String resultDate;
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = format.parse(DateUtils.getCurrentDate(null));
            if(date2!=null&&date2.length()>0){
            	date=format.parse(date2);
            }
            	
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.MINUTE, min);

            String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c
                    .getTime());
            return s;
		}catch(ParseException ex){
			ex.printStackTrace();
			System.out.println("Parse DATE String ERROR!"+ex.getMessage());
		}
		return date2;
	}
	
	/**
	 * 增加日期小时
	 * @param hour
	 * @return
	 */
	public static String addHour(int hour, String date2) {
		String resultDate;
		try{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(DateUtils.getCurrentDate(null));
            if(date2!=null&&date2.length()>0){
            	date=format.parse(date2);
            }
            	
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.HOUR_OF_DAY, hour);

            String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c
                    .getTime());
            return s;
		}catch(ParseException ex){
			ex.printStackTrace();
			System.out.println("Parse DATE String ERROR!"+ex.getMessage());
		}
		return date2;
	}

	/**
	 * 增加日期小时
	 * @param hour
	 * @return
	 */
	public static Date add_Hour(Date date,int hour) {

		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR_OF_DAY, hour);
		return c.getTime();
	}


	/**
	   * 生成当天的年,月,日,时,分,秒的时间字符串
	   * 格式为 20060511101925
	   * @param format String
	   * @return String
	   */
	  public static String getCurrentDate(String format) {
	    if (format == null) {
	      format = PATTERN_CHINESE_NORMAL;
	    }
	    Date utilDate = new Date();
	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    return sdf.format(utilDate);
	  }

	/**
	 * 将日期增加 分钟，返回日期类型
	 * @param date  目标日期
	 * @param min
     * @return
     */
	public static Date addMins(Date date,int min){
		if (null==date){
			return null;
		}
		long result = date.getTime()+min*60* 1000;
		return new Date(result);
	}

	/**
	 * 判断当前时间是否大于date的N小时
	 * @param date
	 * @param day
	 * @return
	 */
	public static boolean outstripDate(String date,int day){

     if (date == null) return false;

		if (getDatePeriod(stringToDate(date),new Date())>=day){
			return true;
		}
		return false;
	}
}
