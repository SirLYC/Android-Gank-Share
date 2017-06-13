package com.lyc.gank.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by 972694341@qq.com on 2017/6/3.
 */

public class TimeUtil {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    private TimeUtil(){}

    public static String getDateString(Date date){
        return format.format(date);
    }

    public static Date getYesterday(Date date){
        return getDayBefore(date, 1);
    }

    public static Date getDayBefore(Date date, int days){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - days);
        try {
            date = format.parse(format.format(calendar.getTime()));
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }
}
