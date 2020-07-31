package com.example.hometrainng.tools;
import android.text.TextUtils;
import org.jetbrains.annotations.NotNull;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DateUtils {

    public static final String DATE_YMD = "yyyy-MM-dd";
    public static final String DATE_FULL = "yyyy-MM-dd HH:mm";
    public static final String DATE_MD = "MM-dd";

    public static String getNowDate(String format) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取推迟或前移的时间
     *
     * @param date yyyy-MM-dd
     */
    public static String getDelayDay(String date, int delay, String format) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            String mdate = "";
            Date d = strToDate(date);
            long myTime = (d.getTime() / 1000) + delay * 24 * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = formatter.format(d);
            return mdate;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * @param strDate yyyy-MM-dd
     * @return
     */
    public static Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_YMD);
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static String dateToStr(Date date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String strDate = null;
        try {
            strDate = formatter.format(date);
        } catch (Exception e) {
           PLog.e(TAG+"/strToDate",e.toString());
        }
        return strDate;
    }

    /**
     * 获取当前日期的一周日期
     *
     * @param date yyyy-MM-dd
     * @return
     */
    public static ArrayList<String> getWeek(String date, String format) {
        ArrayList<String> dates = new ArrayList<>();
        if (TextUtils.isEmpty(date)) {
            System.out.println("date is null or empty");
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_YMD);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (Exception e) {
            PLog.e(TAG+"/getWeek",e.toString());
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        //set the first day of the week is Monday
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        dates.add(formatter.format(cal.getTime()));
        for (int i = 0; i < 6; i++) {
            cal.add(Calendar.DAY_OF_MONTH, 1);
            dates.add(formatter.format(cal.getTime()));
        }
        return dates;
    }

    public static String getMonday(String date, String format) {
        if (TextUtils.isEmpty(date)) {
//            System.out.println("date is null or empty");
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_YMD);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (Exception e) {
            PLog.e(TAG+"/getMonday",e.toString());
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        //set the first day of the week is Monday
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return formatter.format(cal.getTime());
    }

    public static String getSunday(String date, String format) {
        if (TextUtils.isEmpty(date)) {
            return null;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_YMD);
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (Exception e) {
           PLog.e(TAG+"/getSunday",e.toString());
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        //set the first day of the week is Monday
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.add(Calendar.DAY_OF_MONTH, 6);
        return formatter.format(cal.getTime());
    }


    public static int getDayofWeek(String dateTime) {
        Calendar cal = Calendar.getInstance();
        if (TextUtils.isEmpty(dateTime)) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_YMD, Locale.getDefault());
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (ParseException e) {
                date = null;
                PLog.e(TAG+"/getDayofWeek",e.toString());
            }
            if (date != null) {
                cal.setTime(new Date(date.getTime()));
            }
        }
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                return 7;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            default:
                return 0;
        }
    }

    public static int getSchedulePosition(@NotNull String scheduleTime) {
        int position;
        String date = scheduleTime.substring(0, 10);
        int hour = scheduleTime.substring(11, 13).startsWith("0") ? Integer.parseInt(scheduleTime.substring(12, 13)) : Integer.parseInt(scheduleTime.substring(11, 13));
        String min = scheduleTime.substring(14, 16);
        if (hour - 8 >= 0) {
            position = (DateUtils.getDayofWeek(date) - 1) * 24 + (hour - 8) * 2;
            if (min.equals("00")) {
                return position;
            } else if (min.equals("30")) {
                return position + 1;
            }
        }
        return -1;
    }

    public static boolean isDayInWeek(String currentDate, String date) {
        String sunday = getSunday(currentDate, DATE_YMD);
        return (date.compareTo(getMonday(currentDate, DATE_YMD)) >= 0 && sunday != null && sunday.compareTo(date) >= 0);
    }

}
