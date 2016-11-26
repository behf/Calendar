package com.example.behzad.calender;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by behzad on 11/18/2016.
 */

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.behzad.decor.DayDecorator;
import com.example.behzad.utility.CalendarFarsi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DayView extends ImageView {
    private List<DayDecorator> mDayDecoratorList;
    private Date mDate;

    public DayView(Context context) {
        this(context, null, 0);
    }

    public DayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            if (isInEditMode()) {
                return;
            }
        }
    }

    public  void bindIr(CalendarFarsi calendarFarsi){
        int day=calendarFarsi.getIranianDay();
        String name="day"+String.valueOf(day);
        int id = getResources().getIdentifier(name, "drawable",getContext(). getPackageName());
        Drawable drawable = getResources().getDrawable(id);
        setImageDrawable(drawable);

    }


    public void bind(Date date, List<DayDecorator> decorators) {
        //THIS METHODE EXTRACT THE DAY OF DATE THAT RECEIVED AS PARAMETER AND SETS IT AT TEXTVIEW
        this.mDayDecoratorList = decorators;
        this.mDate = date;

        final SimpleDateFormat df = new SimpleDateFormat("d", Locale.getDefault());
        int day = Integer.parseInt(df.format(date));
       // setText(String.valueOf(day));
        String name="day"+String.valueOf(day);
        int id = getResources().getIdentifier(name, "drawable",getContext(). getPackageName());
        Drawable drawable = getResources().getDrawable(id);
        setImageDrawable(drawable);
    }

    public void decorate() {
        //Set custom mDayDecoratorList
        if (null != mDayDecoratorList) {
            for (DayDecorator decorator : mDayDecoratorList) {
                decorator.decorate(this);
            }
        }
    }

    public Date getDate() {
        return mDate;
    }
}