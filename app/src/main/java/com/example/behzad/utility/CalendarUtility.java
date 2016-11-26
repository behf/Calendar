package com.example.behzad.utility;

/**
 * Created by behzad on 11/18/2016.
 */

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author jonatan.salas
 */
public class CalendarUtility {

    /**
     * @param c1
     * @param c2
     * @return
     */
    public static boolean isSameMonth(Calendar c1, Calendar c2) {
        return !(c1 == null || c2 == null) &&
                (c1.get(Calendar.ERA) == c2.get(Calendar.ERA) &&
                        (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)) &&
                        (c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH)));
    }

    /**
     * @param calendar
     * @return
     */
    public static boolean isToday(Calendar calendar) {
        return isSameDay(calendar, Calendar.getInstance());
    }

    /**
     * @param cal1
     * @param cal2
     * @return
     */
    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null)
            throw new IllegalArgumentException("The dates must not be null");
        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)) &&
                (cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)));
    }

    /**
     * @param context
     * @param firstDayOfWeek
     * @return
     */
    public static Calendar getTodayCalendar(Context context, int firstDayOfWeek) {
        Calendar currentCalendar = Calendar.getInstance(context.getResources().getConfiguration().locale);
        currentCalendar.setFirstDayOfWeek(firstDayOfWeek);

        return currentCalendar;
    }

    /**
     * @param currentCalendar
     * @param firstDayOfWeek
     * @return
     */
    public static int getMonthOffset(Calendar currentCalendar, int firstDayOfWeek) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(firstDayOfWeek);
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        int dayPosition = calendar.get(Calendar.DAY_OF_WEEK);

        switch (firstDayWeekPosition) {
            case 1: {
                 return (dayPosition+6)%7;
            }
            case 2: {return (dayPosition+5)%7;
            }
            case 3: {return (dayPosition+4)%7;
            }
            case 4: {return (dayPosition+3)%7;
            }
            case 5: {return (dayPosition+2)%7;
            }
            case 6: {return (dayPosition+1)%7;
            }
            case 7: {return (dayPosition+0)%7;
            }
            default:return (dayPosition+0)%7;
        }


    }

    /**
     * @param weekIndex
     * @param calendar
     * @return
     */
    public static int getWeekIndex(int weekIndex, Calendar calendar) {
        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        switch (firstDayWeekPosition) {
            case 1:
                return weekIndex;
            case 2: {
                if ((weekIndex + 6) % 7 == 0) {
                    return 7;
                } else {
                    return ((weekIndex + 6) % 7);
                }
            }
            case 3: {
                if ((weekIndex + 5) % 7 == 0) {
                    return 7;
                } else {
                    return ((weekIndex + 5) % 7);
                }
            }
            case 4: {
                if ((weekIndex + 4) % 7 == 0) {
                    return 7;
                } else {
                    return ((weekIndex + 4) % 7);
                }
            }
            case 5: {
                if ((weekIndex + 3) % 7 == 0) {
                    return 7;
                } else {
                    return ((weekIndex + 3) % 7);
                }
            }
            case 6: {
                if ((weekIndex + 2) % 7 == 0) {
                    return 7;
                } else {
                    return ((weekIndex + 2) % 7);
                }
            }
            case 7: {
                if ((weekIndex + 1) % 7 == 0) {
                    return 7;
                } else {
                    return ((weekIndex + 1) % 7);
                }
            }
            default:
                Log.v("default", "this is default");
                return weekIndex;
        }

    }

    public static String getCurrentMonth(int monthIndex) {
        final Calendar calendar = Calendar.getInstance(Locale.getDefault());
        final DateFormatSymbols dateFormat = new DateFormatSymbols(Locale.getDefault());
        calendar.add(Calendar.MONTH, monthIndex);

        return dateFormat.getMonths()[calendar.get(Calendar.MONTH)];
    }

}