package com.lyc.gank.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 对时间date进行处理的工具类
 */

public class TimeUtil {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

    private static final SimpleDateFormat lineFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static Calendar calendar = Calendar.getInstance();

    private TimeUtil(){}

    public static Date fromDateLine(String text){
        Date date = null;
        try {
            date = lineFormat.parse(text);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    public static Date fromDateString(String text){
        Date date = null;
        try {
            date = format.parse(text);
        }catch (ParseException e){
            e.printStackTrace();
        }

        return date;
    }

    public static String getDateString(Date date){
        return format.format(date);
    }

    public static String getDateLine(Date date){
        return lineFormat.format(date);
    }

    public static Date getYesterday(Date date){
        return getDayBefore(date, 1);
    }

    public static Date getDayBefore(Date date, int days){
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - days);
        try {
            date = format.parse(format.format(calendar.getTime()));
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }

    public static boolean needRefresh(Date last, Date now){
        if(last == null)
            return true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        int hourNow = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.setTime(last);
        int dayLast = calendar.get(Calendar.DAY_OF_MONTH);
        int hourLast = calendar.get(Calendar.HOUR_OF_DAY);
        //每天13点更新
        return (dayLast != dayNow && hourLast <= 13)
                || (dayLast == dayNow && hourNow > 13 && hourLast <= 13);
    }

    public static boolean imgNeedRefresh(Date last, Date now){
        if(last == null)
            return true;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        int dayNow = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(last);
        int dayLast = calendar.get(Calendar.DAY_OF_MONTH);
        return dayNow != dayLast;
    }

    public static String publishTime(String utcTime){
        if(utcTime == null)
            return null;

        if(utcTime.length() < 10)
            return utcTime;

        return utcTime.substring(0, 10);
    }
}
