package com.example.behzad.calender;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Dialog;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.behzad.utility.CalendarFarsi;
import com.example.behzad.utility.CalendarFarsiUtility;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    CalendarView calendarView;

    Calendar dateandtime;
    int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        i = 0;


        calendarView = (CalendarView) findViewById(R.id.calendar_view);
        Log.v("this is MAIN", " BEFORE SET FIRSTDAY__FIRSTDAY= " + calendarView.mFirstDayOfWeek);
        calendarView.setFirstDayOfWeek(Calendar.SATURDAY);
        Log.v("this is MAIN", " AFTER SET FIRSTDAY__FIRSTDAY= " + calendarView.mFirstDayOfWeek);
        calendarView.setIsOverflowDateVisible(false);
        dateandtime = Calendar.getInstance();
        final CalendarFarsi calendarFarsi = new CalendarFarsi(dateandtime);

        //  calendarView.setCurrentDay(new Date(System.currentTimeMillis()),3000);
        calendarView.setBackButtonColor(R.color.colorAccent);
        calendarView.setNextButtonColor(R.color.colorAccent);
        calendarView.refreshCalendar(Calendar.getInstance(Locale.getDefault()));
        calendarView.setOnDateLongClickListener(new CalendarView.OnDateLongClickListener() {
            @Override
            public void onDateLongClick(@NonNull Date selectedDate) {
                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

            }
        });


        calendarView.setOnMonthChangedListener(new CalendarView.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(@NonNull Date monthDate, CalendarFarsi mCalendarFarsi) {
                SimpleDateFormat df = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

            }
        });


        calendarView.setOnGregorianMonthTitleClickListener(new CalendarView.OnGregorianMonthTitleClickListener() {
            @Override
            public void onGregorianMonthTitleClick(@NonNull Date MonthDate) {
                gDatePickerDialog dp = new gDatePickerDialog(MainActivity.this,
                        dateandtime, new gDatePickerDialog.DatePickerListner() {

                    @Override
                    public void OnDoneButton(Dialog datedialog, CalendarFarsi c) {
                        datedialog.dismiss();
                        GregorianCalendar calendar =new GregorianCalendar();
                        CalendarFarsi calendarFarsi1 = new CalendarFarsi(calendar);
                        calendar.set(c.getGregorianYear(), c.getGregorianMonth() - 1, c.getGregorianDay());
                        calendarView.ummalquraCalendaruCal.setTime(calendar.getTime());

                        calendarView.mCalendarFarsi = c;
                        int currentmontindex = (c.getIranianYear() - calendarFarsi.getIranianYear()) * 12 + (c.getIranianMonth() - calendarFarsi.getIranianMonth());
                        calendarView.mCurrentMonthIndex = currentmontindex;
                        calendarView.LastSelectedDayIndex = CalendarFarsiUtility.getMonthOffset(c, calendarView.mFirstDayOfWeek) + c.getIranianDay();
                        calendarView.refreshCalendar(calendar);

                    }

                    @Override
                    public void OnCancelButton(Dialog datedialog) {
                        // TODO Auto-generated method stub
                        datedialog.dismiss();
                    }
                });
                dp.show();
            }
        });

        calendarView.setOnHijriMonthTitleClickListener(new CalendarView.OnHijriMonthTitleClickListener() {
            @Override
            public void onHijriMonthTitleClick(@NonNull Date MonthDate) {

            }
        });

        calendarView.setOnMonthTitleClickListener(new CalendarView.OnMonthTitleClickListener() {
            @Override
            public void onMonthTitleClick(@NonNull Date selectedDate) {
                DatePickerDailog dp = new DatePickerDailog(MainActivity.this,
                        dateandtime, new DatePickerDailog.DatePickerListner() {

                    @Override
                    public void OnDoneButton(Dialog datedialog, CalendarFarsi c) {
                        datedialog.dismiss();
                        GregorianCalendar calendar =new GregorianCalendar();
                        CalendarFarsi calendarFarsi1 = new CalendarFarsi(calendar);
                        calendar.set(c.getGregorianYear(), c.getGregorianMonth() - 1, c.getGregorianDay());
                        calendarView.ummalquraCalendaruCal.setTime(calendar.getTime());

                        calendarView.mCalendarFarsi = c;
                        int currentmontindex = (c.getIranianYear() - calendarFarsi.getIranianYear()) * 12 + (c.getIranianMonth() - calendarFarsi.getIranianMonth());
                        calendarView.mCurrentMonthIndex = currentmontindex;
                        calendarView.LastSelectedDayIndex = CalendarFarsiUtility.getMonthOffset(c, calendarView.mFirstDayOfWeek) + c.getIranianDay();
                        calendarView.refreshCalendar(calendar);
                    }

                    @Override
                    public void OnCancelButton(Dialog datedialog) {
                        // TODO Auto-generated method stub
                        datedialog.dismiss();
                    }
                });
                dp.show();
            }
        });
        calendarView.setOnDateClickListener(new CalendarView.OnDateClickListener() {
            @Override
            public void onDateClick(@NonNull Date selectedDate, CalendarFarsi calendarFarsi) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        if (i == 1) {
            super.onBackPressed();
        } else {
            Toast.makeText(this, "لطفا کلید بازگشت را مجددا فشار دهید", Toast.LENGTH_SHORT).show();
            i++;
        }


    }
}
