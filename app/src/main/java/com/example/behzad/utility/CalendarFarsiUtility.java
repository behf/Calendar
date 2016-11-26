package com.example.behzad.utility;

import android.content.Context;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by behzad on 11/21/2016.
 */

public class CalendarFarsiUtility {

    public static boolean isSameMonth(Calendar calendar,int IranianYear,int IranianMonth){
         CalendarFarsi calendarFarsi=new CalendarFarsi(calendar);
        int IrMonth= calendarFarsi.getIranianMonth();
        int IrYear=calendarFarsi.getIranianYear();

        if (IranianYear==IrYear&&IranianMonth==IrMonth){

            return true;
        }else return false;


    }

    public static boolean isSameMonth(CalendarFarsi calendarFarsi,int IranianYear,int IranianMonth){
       if (calendarFarsi.getIranianYear()==IranianYear&&calendarFarsi.getIranianMonth()==IranianMonth)
           return true;
        else
            return false;

    }


     public  static int getMonthOffset(CalendarFarsi calendarFarsi,int FIRSTDAYOFWEEK){
         calendarFarsi.setIr(CalendarFarsi.DAY_OF_MONTH,1);
         int dayPosition =  calendarFarsi.getDayOfWeek();
         int firstDayWeekPosition=FIRSTDAYOFWEEK;
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


public static boolean isToday(CalendarFarsi calendarFarsi){

    CalendarFarsi Today=new CalendarFarsi(Calendar.getInstance());
    if (calendarFarsi.getIranianDate().equals(Today.getIranianDate()))return true;
    else return false;

}


             public static   int getMonthOffset(  Context context,Calendar calendar, int firstDayOfWeek){
                 CalendarFarsi calendarFarsi=new CalendarFarsi(calendar);
                 calendarFarsi.setIr(CalendarFarsi.DAY_OF_MONTH,1);
                 int firstDayWeekPosition=firstDayOfWeek;
                 int dayPosition =  calendarFarsi.getDayOfWeek();
                 //Toast.makeText(context, String.valueOf(calendarFarsi.getDayOfWeek()), Toast.LENGTH_SHORT).show();
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

}
