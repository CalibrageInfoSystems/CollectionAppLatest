package com.oilpalm3f.mainapp.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


//Not Using
public class TimeUtils {
    public static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");

    private static final ThreadLocal<DateFormat> ISO_8601_FORMAT = new ThreadLocal<DateFormat>() {
        @Override protected DateFormat initialValue() {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df.setTimeZone(UTC_TIME_ZONE);
            return df;
        }
    };

    // old Peel format
    public static final ThreadLocal<SimpleDateFormat> dateFormatYMDHMS_GMT = new ThreadLocal<SimpleDateFormat>() {
        @Override protected SimpleDateFormat initialValue() {
            SimpleDateFormat gmtdateformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            gmtdateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return gmtdateformat;
        }
    };

    private static final long THIRTY_MINUTES = 30 * 60 * 1000;
    public static final long TWO_DAYS = 2 * 24 * 60 * 60 * 1000;
    public static final long ONE_HOUR = 60 * 60 * 1000;
    public static final long ONE_DAY = 24 * ONE_HOUR;

    public static Date parseAsIso8601(String date) {
        try {
            return ISO_8601_FORMAT.get().parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatAsIso8601(Date date) {
        return ISO_8601_FORMAT.get().format(date);
    }

    public static String convertPeelFormatGmtIntoIso_8601_Format(String dateFormatYMDHMS_GMT) throws ParseException {
        Date dateOldTime = TimeUtils.dateFormatYMDHMS_GMT.get().parse(dateFormatYMDHMS_GMT);
        return formatAsIso8601(dateOldTime);
    }

    /**
     * Returns a copy of the specified date with the time aligned to the last 30 minutes boundary.
     * So, 8:17pm becomes 8:00pm, and 8:31pm becomes 8:30pm. 8:30pm will remain 8:30pm.
     */
    public static Date alignToHalfHourBoundary(Date date) {
        long time = date.getTime();
        time /= THIRTY_MINUTES;
        time *= THIRTY_MINUTES;
        return new Date(time);
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {
        }

    }
}
