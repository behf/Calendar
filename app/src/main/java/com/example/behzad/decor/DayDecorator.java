package com.example.behzad.decor;

/**
 * Created by behzad on 11/18/2016.
 */

import android.support.annotation.NonNull;

import com.example.behzad.calender.DayView;

/**
 * @author jonatan.salas
 */
public interface DayDecorator {

    /**
     *
     * @param cell
     */
    void decorate(@NonNull DayView cell);
}