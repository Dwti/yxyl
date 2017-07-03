package com.yanxiu.gphone.student.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class TimeUtils {

    /**
     * 时间转换分：秒
     */
    public static String stringForTimeNoHour(int timeMs) {

        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

        try {
            int totalSeconds = timeMs;

            int seconds = totalSeconds % 60;
            int minutes = totalSeconds / 60;

            formatBuilder.setLength(0);

            return formatter.format("%02d:%02d", minutes, seconds).toString();
        } finally {
            formatter.close();
        }
    }

    public static String getTimeLongYMD(long date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date mDate = null;
        String ymd;
        try {
//            long time = Long.parseLong(date);
            long time = date;
            mDate = new Date(time);
            ymd = format.format(mDate);
        } catch (Exception e) {
            mDate = new Date();
            ymd = format.format(mDate);
        }
        return ymd;
    }

    /**
     * 时间转换分：秒
     */
    public static String formatTime(int timeMs) {

        StringBuilder formatBuilder = new StringBuilder();
        Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());

        try {
//            int totalSeconds = timeMs;
            int sec = timeMs % 60;
            timeMs = timeMs / 60;
            int min = timeMs % 60;
            timeMs = timeMs / 60;

            formatBuilder.setLength(0);

            return formatter.format("%02d:%02d:%02d", timeMs, min, sec).toString();
        } finally {
            formatter.close();
        }
    }


}
