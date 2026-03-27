package com.n2.gameshop.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public final class DateUtils {
    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    private DateUtils() {
    }

    public static String today() {
        return DATE_FORMAT.format(Calendar.getInstance().getTime());
    }

    public static String startOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        return DATE_FORMAT.format(cal.getTime());
    }

    public static String startOfMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return DATE_FORMAT.format(cal.getTime());
    }

    public static String startOfYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, 1);
        return DATE_FORMAT.format(cal.getTime());
    }
}

