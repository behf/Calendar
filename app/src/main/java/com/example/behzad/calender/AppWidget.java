package com.example.behzad.calender;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.example.behzad.utility.CalendarFarsi;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import static android.util.TypedValue.COMPLEX_UNIT_SP;

/**
 * Implementation of App Widget functionality.
 */
public class AppWidget extends AppWidgetProvider {
    static Context mContext;
    public static final String APP_WIDGET_UPDATE = "com.example.behzad.calendar.AppWidget.APP_WIDGET_UPDATE";

    private PendingIntent createUpdateInent(Context context) {
        mContext = context;
        Intent intent = new Intent(APP_WIDGET_UPDATE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        mContext = context;
        Calendar calendar = Calendar.getInstance();
        CalendarFarsi calendarFarsi = new CalendarFarsi(calendar);

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.app_widget);
        views.setImageViewBitmap(R.id.textview_sDate, buildUpdate(calendarFarsi.getIraniandate()));
        views.setTextViewText(R.id.textview_time, calendarFarsi.getCurrentTime(true));
        views.setTextViewText(R.id.textview_gDate, calendarFarsi.getGregoriandate());
        views.setTextViewText(R.id.textview_hDate, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        mContext = context;
        context.startService(new Intent(context, UpdateService.class));
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        mContext = context;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 1);

        //update widget every 1 second
        alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000, createUpdateInent(context));
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(createUpdateInent(context));

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        mContext = context;
        if (APP_WIDGET_UPDATE.equals(intent.getAction())) {
            context.startService(new Intent(context, UpdateService.class));
        }
    }

    public static Bitmap buildUpdate(String time) {
        Bitmap myBitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_4444);

        Bitmap result =Bitmap.createBitmap(myBitmap,40, 10, 350,85);
        Canvas myCanvas = new Canvas(result);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(mContext.getAssets(), "fonts/B Nazanin Bold.ttf");
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTypeface(clock);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(55);
       paint.setTextAlign(Paint.Align.CENTER);
        myCanvas.drawText(time, 180,60, paint);

        return result;
    }

    public static class UpdateService extends Service {
        GregorianCalendar gregorianCalendar;
        UmmalquraCalendar ummalquraCalendar;

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public void onStart(Intent intent, int startId) {
            super.onStart(intent, startId);
            ummalquraCalendar = new UmmalquraCalendar();
            gregorianCalendar = new GregorianCalendar();
            RemoteViews updateviews = buildUpdate(this);
            ComponentName widget = new ComponentName(this, AppWidget.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(widget, updateviews);

        }

        private RemoteViews buildUpdate(Context context) {
            RemoteViews updateiews = new RemoteViews(context.getPackageName(), R.layout.app_widget);
            Calendar c = Calendar.getInstance();
            ummalquraCalendar.setTime(gregorianCalendar.getTime());
            CalendarFarsi calendarFarsi = new CalendarFarsi(c);
            c.setTime(new Date());
            int min=c.get(Calendar.MINUTE);
            int hour=c.get(Calendar.HOUR_OF_DAY);
            updateiews.setTextViewText(R.id.textview_time, String.valueOf(hour)+":"+( min < 10 ? "0" : "")+String.valueOf(min));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                updateiews.setTextViewTextSize (R.id.textview_time, COMPLEX_UNIT_SP,
                38.0f);
            }

            updateiews.setImageViewBitmap(R.id.textview_sDate, AppWidget.buildUpdate(calendarFarsi.getlongIraniandate()));
            updateiews.setTextViewText(R.id.textview_gDate, calendarFarsi.getGregoriandate());
            updateiews.setTextViewText(R.id.textview_hDate, getHijridate());

            return updateiews;
        }

        public String getHijridate() {

            return (ummalquraCalendar.get(Calendar.DAY_OF_MONTH) + " " + getHijriMonth() + " " + ummalquraCalendar.get(Calendar.YEAR));
        }

        public String getHijriMonth() {
            String hijriMONTH = ummalquraCalendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());

            switch (hijriMONTH) {
                case "Muh":
                    return "محرم";
                case "Saf":
                    return "صفر";
                case "Rab-I":
                    return "ربیع الاول";
                case "Rab-II":
                    return "ربیع الثانی";
                case "Jum-I":
                    return "جمادی الاول";
                case "Jum-II":
                    return "جمادی الثانی";
                case "Raj":
                    return "رجب";
                case "Sha":
                    return "شعبان";
                case "Ram":
                    return "رمضان";
                case "Shw":
                    return "شوال";
                case "Thul-Q":
                    return "ذی القعده";
                case "Thul-H":
                    return "ذی الحجه";
                default:
                    return "";

            }
        }
    }
}

