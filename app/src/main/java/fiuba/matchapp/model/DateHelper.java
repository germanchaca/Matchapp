package fiuba.matchapp.model;

import android.content.Context;
import android.text.format.DateUtils;

import java.util.Calendar;
import java.util.Date;

import fiuba.matchapp.R;

public class DateHelper {

    public static String getTimeStamp(String dateStr, Context context) {
        Long timestamp = Long.parseLong(dateStr);

        return DateHelper.convertTimeToDateString(context,timestamp,true, true, true, true, false, false, false);
    }

    public static String getTimeStamp(Long dateStr, Context context) {

        return DateHelper.convertTimeToDateString(context,dateStr,true, true, true, true, false, false, false);
    }

    public static String convertTimeToDateString(Context context, long time, boolean showInsideWeekString, boolean showYear, boolean showMonthShort, boolean showTime, boolean showTimeShort, boolean showWeekday, boolean showWeekdayShort) {
        if (time == -1 || time == 0 || context == null) {
            return "";
        }
        Date date;
        Calendar calendarTime;
        long when;
        int flags;
        if (showInsideWeekString) {
            Calendar todayCal = Calendar.getInstance();
            Calendar yesterdayCal = Calendar.getInstance();
            yesterdayCal.add(6, -1);
            Calendar before2DayCal = Calendar.getInstance();
            before2DayCal.add(6, -2);
            Calendar before3DayCal = Calendar.getInstance();
            before3DayCal.add(6, -3);
            Calendar before4DayCal = Calendar.getInstance();
            before4DayCal.add(6, -4);
            Calendar before5DayCal = Calendar.getInstance();
            before5DayCal.add(6, -5);
            Calendar before6DayCal = Calendar.getInstance();
            before6DayCal.add(6, -6);
            Calendar tomorrowCal = Calendar.getInstance();
            tomorrowCal.add(6, 1);
            date = new Date(1000 * time);
            calendarTime = Calendar.getInstance();
            calendarTime.setTime(date);
            if (calendarTime.get(1) == todayCal.get(1) && calendarTime.get(6) == todayCal.get(6)) {
                return DateUtils.formatDateTime(context, calendarTime.getTimeInMillis(), 0 | 1);
            }
            String timeString;
            if (calendarTime.get(1) == yesterdayCal.get(1) && calendarTime.get(6) == yesterdayCal.get(6)) {
                timeString = context.getText(R.string.label_yesterday).toString();
                if (!showTime) {
                    return timeString;
                }
                return timeString + " " + DateUtils.formatDateTime(context, calendarTime.getTimeInMillis(), 0 | 1);
            }
            if ((calendarTime.get(1) == before2DayCal.get(1) && calendarTime.get(6) == before2DayCal.get(6)) || ((calendarTime.get(1) == before3DayCal.get(1) && calendarTime.get(6) == before3DayCal.get(6)) || ((calendarTime.get(1) == before4DayCal.get(1) && calendarTime.get(6) == before4DayCal.get(6)) || ((calendarTime.get(1) == before5DayCal.get(1) && calendarTime.get(6) == before5DayCal.get(6)) || (calendarTime.get(1) == before6DayCal.get(1) && calendarTime.get(6) == before6DayCal.get(6)))))) {
                when = calendarTime.getTimeInMillis();
                flags = 0 | 2;
                if (showTime) {
                    flags |= 1;
                }
                return DateUtils.formatDateTime(context, when, flags);
            }
        }
        date = new Date(1000 * time);
        calendarTime = Calendar.getInstance();
        calendarTime.setTime(date);
        when = calendarTime.getTimeInMillis();
        flags = 0 | 16;
        if (showYear) {
            flags |= 4;
        }
        if (showMonthShort) {
            flags |= 65536;
        }
        if (showTime) {
            flags |= 1;
        }
        if (showTimeShort) {
            flags |= 16384;
        }
        if (showWeekday) {
            flags |= 2;
        }
        if (showWeekdayShort) {
            flags |= 32768;
        }
        return DateUtils.formatDateTime(context, when, flags);
    }
}
