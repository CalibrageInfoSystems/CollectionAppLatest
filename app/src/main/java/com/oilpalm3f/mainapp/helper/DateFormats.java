package com.oilpalm3f.mainapp.helper;


import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


//Not Using
public class DateFormats {

    public static final ThreadLocal<SimpleDateFormat> dateFormatLocal = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        }
    };
    public static final ThreadLocal<SimpleDateFormat> dateFormatLocalRecommended = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH", Locale.US);
        }
    };
    public static final ThreadLocal<SimpleDateFormat> dateFormatYMDHMS = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        }
    };
    public static final ThreadLocal<SimpleDateFormat> dateFormatYMDHMS_GMT = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            final SimpleDateFormat gmtdateformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
            gmtdateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return gmtdateformat;
        }
    };
    public static final ThreadLocal<SimpleDateFormat> dateFormatYMDHMS_UTC = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            final SimpleDateFormat utcdateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss" +
                    ".sszzz", Locale.US);
            utcdateformat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return utcdateformat;
        }
    };
    public static final ThreadLocal<SimpleDateFormat> dateFormatYMDHM = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        }
    };
    public static final ThreadLocal<SimpleDateFormat> hourMinuteAmPmFormatter = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("h:mm a", Locale.US);
        }
    };
    public static final ThreadLocal<SimpleDateFormat> hourMinute24HourFormatter = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("H:mm", Locale.US);
        }
    };
    public static final ThreadLocal<SimpleDateFormat> hourMinuteFormatter = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("hh:mm", Locale.US);
        }
    };
    public static final ThreadLocal<SimpleDateFormat> longDays = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("EEEE", Locale.US);
        }
    };
    public static final ThreadLocal<SimpleDateFormat> GMTDateFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            final SimpleDateFormat gmtdateformat = new SimpleDateFormat("yyyyMMddHHmm00", Locale.US);
            gmtdateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return gmtdateformat;
        }
    };

    public static final ThreadLocal<SimpleDateFormat> fullMonthDayFormat = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("MMMMM d", Locale.US);
        }
    };
    public static final SimpleDateFormat localTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    public static final int YESTERDAY = 0;
    public static final int TODAY = 1;
    public static final int TOMORROW = 2;
    private static final String LOG_TAG = DateFormats.class.getName();

    private static ThreadLocal<Locale> currentLocale = new ThreadLocal<Locale>() {
        protected Locale initialValue() {
            return Locale.US;
        }
    };


    public static String getCurrentDatetime() {
        final java.util.GregorianCalendar cal = new java.util.GregorianCalendar();

        final String dt = dateFormatLocal.get().format(cal.getTime());

        return dt;

    }

    public static String getStartOfTheDay() {
        final java.util.GregorianCalendar now = new java.util.GregorianCalendar();
        java.util.GregorianCalendar today = new java.util.GregorianCalendar(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH));
        return dateFormatYMDHMS.get().format(today.getTime());
    }

    public static String getNextHalfHourDatetime(final String datetime) {
        final java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        try {
            cal.setTime(dateFormatYMDHMS.get().parse(datetime));
            cal.add(Calendar.MINUTE, 30);
            return dateFormatYMDHMS.get().format(cal.getTime());
        } catch (Exception e) {
            Log.e(LOG_TAG, LOG_TAG, e);
        }
        return datetime;
    }


    public static long convertRecommendedTimeToMilliseconds(String date) {
        try {
            return dateFormatYMDHM.get().parse(date).getTime();
        } catch (ParseException e) {
            Log.e(LOG_TAG, LOG_TAG, e);
        }

        return 0;
    }

    public static String getCurrentRecommendedDatetimePrefix() {
        java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
        return dateFormatYMDHMS_GMT.get().format(cal.getTime());
    }


    public static String getCurrentDatetimePrefix() {
        long timeInMillis = System.currentTimeMillis();
        Calendar cal1 = Calendar.getInstance();
        cal1.setTimeInMillis(timeInMillis);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        String dateforrow = dateFormat.format(cal1.getTime());
        return dateforrow;
    }

    // convert 201102211330 to 201102212130
    public static String convertLocalToGMT(String local) {
        try {
            return GMTDateFormat.get().format(dateFormatLocal.get().parse(local));
        } catch (Exception e) {
            Log.e(LOG_TAG, LOG_TAG, e);
        }
        return local;
    }


    public static Date convertGMTToLocalDate(final String gmt) {
        try {
            return dateFormatYMDHMS_GMT.get().parse(gmt);
        } catch (Exception e) {
            Log.e(LOG_TAG, LOG_TAG, e);
        }
        return null;
    }

    public static Date convertUTCToLocalDate(final String utc) {
        try {
            return dateFormatYMDHMS_UTC.get().parse(utc);
        } catch (Exception e) {
            Log.e(LOG_TAG, LOG_TAG, e);
        }
        return null;
    }

    public static String getStringForUTCByLong(long start) {
        try {
            return dateFormatYMDHMS_UTC.get().format(new Date(start));
        } catch (Exception e) {
            Log.e(LOG_TAG, LOG_TAG, e);
        }

        return null;
    }

    public static String convertUTCToGMT(String utc) {
        try {
            return dateFormatYMDHMS_GMT.get().format(dateFormatYMDHMS_UTC.get().parse(utc));
        } catch (Exception e) {
            Log.e(LOG_TAG, LOG_TAG, e);
        }

        return null;
    }


    /**
     * @return true if time2 is later than time1
     */
    public static boolean compareDateStrings(String time1, String time2) {
        try {
            Date date1 = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(time1);
            Date date2 = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).parse(time2);
            long millisecondsEarlier = date1.getTime();
            long millisecondsLater = date2.getTime();
            return millisecondsEarlier < millisecondsLater;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }
}
