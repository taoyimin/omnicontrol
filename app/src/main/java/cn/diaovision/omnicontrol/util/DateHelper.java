package cn.diaovision.omnicontrol.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/* A simple datehelper for building & converting date from & to strings
 * Created by liulingfeng on 2017/4/11.
 */

public class DateHelper {
    static private DateHelper instance;

    private DateHelper(){

    }

    static public DateHelper getInstance(){
        if (instance == null) {
            instance = new DateHelper();
        }
        return instance;
    }

    public Date buildDate(String dateStr){
        DateFormat format = new SimpleDateFormat("yyyy-mm-dd-hh-mm");
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.YEAR);
    }

    public int getMonth(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MONTH);
    }

    public int getDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    public int getHour(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public int getMin(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }
}
