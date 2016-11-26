package com.example.behzad.calender;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by behzad on 11/18/2016.
 */
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;


import com.example.behzad.decor.DayDecorator;
import com.example.behzad.utility.CalendarFarsi;
import com.example.behzad.utility.CalendarFarsiUtility;
import com.example.behzad.utility.CalendarUtility;
import com.github.msarhan.ummalqura.calendar.UmmalquraCalendar;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/***
 * Custom CalendarView class.
 *
 * @author jonatan.salas
 */
public class CalendarView extends LinearLayout {

    public static final String KEY_SUPER_STATE = "KEY_SUPER_STATE";
    public static final String IRANIAN_YEAR = "IRANIAN_YEAR";
    public static final String IRANIAN_MONTH = "IRANIAN_MONTH";
    public static final String IRANIAN_DAY = "IRANIAN_DAY";
    public static final String GREGORIAN_YEAR = "GREGORIAN_YEAR";
    public static final String GREGORIAN_MONTH = "GREGORIAN_MONTH";
    public static final String GREGORIAN_DAY = "GREGORIAN_DAY";
    public static final String CURRENT_MONTH_INDEX = "CURRENT_MONTH_INDEX";
    /**
     * Indicates that the CalendarView is in an idle, settled state. The current page
     * is fully in view and no animation is in progress.
     */
    int SCROLL_STATE_IDLE = 0;

    /**
     * Indicates that the CalendarView is currently being dragged by the user.
     */
    int SCROLL_STATE_DRAGGING = 1;

    /**
     * Indicates that the CalendarView is in the process of settling to a final position.
     */
    int SCROLL_STATE_SETTLING = 2;

    boolean USE_CACHE = false;
    int MIN_DISTANCE_FOR_FLING = 25; // dips
    int DEFAULT_GUTTER_SIZE = 16; // dips
    int MIN_FLING_VELOCITY = 400; // dips

    /**
     * Sentinel value for no current active pointer.
     */
    int INVALID_POINTER = -1;

    // If the CalendarView is at least this close to its final position, complete the scroll
    // on touch down and let the user interact with the content inside instead of
    // "catching" the flinging Calendar.
    int CLOSE_ENOUGH = 2; // dp
    private int CurrentDayIndex;
    public int LastSelectedDayIndex = 0;
    private int lastSelectedDayMonthOffset = 0;
    private int lastMonthOffset = 0;
    private int lastDefaultSelectedDayIndex = 0;
    private boolean mScrollingCacheEnabled;
    private boolean mIsBeingDragged;
    private boolean mIsUnableToDrag;
    private int mDefaultGutterSize;
    private int mTouchSlop;
    public CalendarFarsi mCalendarFarsi;
    /**
     * Position of the last motion event.
     */
    private float mLastMotionX;
    private float mLastMotionY;
    private float mInitialMotionX;
    private float mInitialMotionY;

    private Scroller mScroller;

    /**
     * ID of the active pointer. This is used to retain consistency during
     * drags/flings if multiple pointers are used.
     */
    private int mActivePointerId = INVALID_POINTER;

    /**
     * Determines speed during touch scrolling
     */
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private int mFlingDistance;
    private int mCloseEnough;

    private int mScrollState = SCROLL_STATE_IDLE;

    private final Runnable mEndScrollRunnable = new Runnable() {
        public void run() {
            setScrollState(SCROLL_STATE_IDLE);
        }
    };

    // Gesture Detector used to handle Swipe gestures.
    private GestureDetectorCompat mGestureDetector;
    private Context mContext;
    private View mView;
    private View mainView;
    private ImageView mNextButton;
    private ImageView mBackButton;

    //Listeners used by the Calendar...
    private OnGregorianMonthTitleClickListener mOnGregorianMonthTitleClickListener;
    private OnHijriMonthTitleClickListener mOnHijriMonthTitleClickListener;
    private OnMonthTitleClickListener mOnMonthTitleClickListener;
    private OnDateClickListener mOnDateClickListener;
    private OnDateLongClickListener mOnDateLongClickListener;
    private OnMonthChangedListener mOnMonthChangedListener;

    private GregorianCalendar mgregorianCalendar;
    private Calendar mCalendar;
    private Date mLastSelectedDay;

    //Customizable variables...
    private Typeface mTypeface;
    private int mDisabledDayBackgroundColor;
    private int mDisabledDayTextColor;
    private int mCalendarBackgroundColor;
    private int mSelectedDayBackground;
    private int mWeekLayoutBackgroundColor;
    private int mCalendarTitleBackgroundColor;
    private int mSelectedDayTextColor;
    private int mCalendarTitleTextColor;
    private int mDayOfWeekTextColor;
    private int mCurrentDayOfMonth;
    private int mWeekendColor;
    private int mWeekend;
    private int commonDayOfWeekColor;
    Calendar ummalquraCalendaruCal;

    private List<DayDecorator> mDecoratorsList = null;
    private boolean mIsOverflowDateVisible = true;
    public int mFirstDayOfWeek = Calendar.FRIDAY;
    public int mCurrentMonthIndex = 0;

    // Day of weekend
    private int[] mTotalDayOfWeekend;

    // true for ordinary day, false for a weekend.
    private boolean mIsCommonDay;

    /**
     * Constructor with arguments. It receives a
     * Context used to get the resources.
     *
     * @param context - the context used to get the resources.
     */
    public CalendarView(Context context) {
        this(context, null);

        //Initialize the gesture listener needed..
        mGestureDetector = new GestureDetectorCompat(context, new CalendarGestureDetector());
    }

    /**
     * Constructor with arguments. It receives a
     * Context used to get the resources.
     *
     * @param context - the context used to get the resources.
     * @param attrs   - attribute set with custom styles.
     */
    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        //Initialize the gesture listener needed..
        mGestureDetector = new GestureDetectorCompat(context, new CalendarGestureDetector());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            if (isInEditMode()) {
                return;
            }
        }

        getAttributes(attrs);
        init();
    }

    /***
     * Method that gets and set the attributes of the CalendarView class.
     *
     * @param attrs - Attribute set object with custom values to be setted
     */
    private void getAttributes(AttributeSet attrs) {
        final TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.MaterialCalendarView, 0, 0);

        final int white = ContextCompat.getColor(mContext, android.R.color.white);
        final int black = ContextCompat.getColor(mContext, android.R.color.black);
        final int dayDisableBackground = ContextCompat.getColor(mContext, R.color.day_disabled_background_color);
        final int dayDisableTextColor = ContextCompat.getColor(mContext, R.color.day_disabled_text_color);
        final int daySelectedBackground = ContextCompat.getColor(mContext, R.color.selected_day_background);
        final int dayCurrent = ContextCompat.getColor(mContext, R.color.current_day_of_month);
        final int weekendColor = ContextCompat.getColor(mContext, R.color.weekend_color);

        try {
            mCalendarBackgroundColor = a.getColor(R.styleable.MaterialCalendarView_calendarBackgroundColor, white);
            mCalendarTitleBackgroundColor = a.getColor(R.styleable.MaterialCalendarView_titleLayoutBackgroundColor, white);
            mCalendarTitleTextColor = a.getColor(R.styleable.MaterialCalendarView_calendarTitleTextColor, black);
            mWeekLayoutBackgroundColor = a.getColor(R.styleable.MaterialCalendarView_weekLayoutBackgroundColor, white);
            mDayOfWeekTextColor = a.getColor(R.styleable.MaterialCalendarView_dayOfWeekTextColor, black);
            mDisabledDayBackgroundColor = a.getColor(R.styleable.MaterialCalendarView_disabledDayBackgroundColor, dayDisableBackground);
            mDisabledDayTextColor = a.getColor(R.styleable.MaterialCalendarView_disabledDayTextColor, dayDisableTextColor);
            mSelectedDayBackground = a.getColor(R.styleable.MaterialCalendarView_selectedDayBackgroundColor, daySelectedBackground);
            mSelectedDayTextColor = a.getColor(R.styleable.MaterialCalendarView_selectedDayTextColor, white);
            mCurrentDayOfMonth = a.getColor(R.styleable.MaterialCalendarView_currentDayOfMonthColor, dayCurrent);
            commonDayOfWeekColor = a.getColor(R.styleable.MaterialCalendarView_commonDayOfWeekColor, white);
            mWeekendColor = a.getColor(R.styleable.MaterialCalendarView_weekendColor, weekendColor);
            a.getResourceId(R.styleable.MaterialCalendarView_weekendresource, 0);
            mWeekend = a.getInteger(R.styleable.MaterialCalendarView_weekend, 6);
        } finally {
            if (null != a) {
                a.recycle();
            }
        }
    }

    /**
     * This method init all necessary variables and Views that our Calendar is going to use.
     */
    private void init() {
        mScroller = new Scroller(mContext, null);
        Log.v("this is INIT", "I AM INIT");
        //Variables associated to handle touch events..
        final ViewConfiguration configuration = ViewConfiguration.get(mContext);
        final float density = mContext.getResources().getDisplayMetrics().density;

        //Variables associated to Swipe..
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
        mCloseEnough = (int) (CLOSE_ENOUGH * density);
        mDefaultGutterSize = (int) (DEFAULT_GUTTER_SIZE * density);
        mCalendar = Calendar.getInstance(Locale.getDefault());
        mgregorianCalendar = new GregorianCalendar();
        mCalendarFarsi = new CalendarFarsi(mCalendar);
        ummalquraCalendaruCal = new UmmalquraCalendar();
        ummalquraCalendaruCal.setTime(mgregorianCalendar.getTime());
        //Inflate current view..
        mView = LayoutInflater.from(mContext).inflate(R.layout.calendar_view, this, true);


        //Get buttons for Calendar and set it´s listeners..
        mBackButton = (ImageView) mView.findViewById(R.id.left_button);
        mNextButton = (ImageView) mView.findViewById(R.id.right_button);

        mBackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentMonthIndex--;
                mCalendar = Calendar.getInstance(Locale.getDefault());
                mCalendarFarsi = new CalendarFarsi(mCalendar);
                int IrMonth = mCalendarFarsi.getIranianMonth();
                int IrDay = mCalendarFarsi.getIranianDay();
                int addyearIr = ((mCurrentMonthIndex + IrMonth));
                if (addyearIr > 0) {
                    mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() + (addyearIr / 12), (addyearIr % 12), IrDay);
                } else if (addyearIr < 0) {
                    mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() - ((Math.abs(addyearIr) / 12) + 1), 12 - (Math.abs(addyearIr) % 12), IrDay);
                } else if (addyearIr == 0) {
                    mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() - ((addyearIr / 12) + 1), 12, IrDay);
                }
                int curentmontOffset = CalendarFarsiUtility.getMonthOffset(mCalendarFarsi, mFirstDayOfWeek);
                mCalendarFarsi.setIr(CalendarFarsi.DAY_OF_MONTH, IrDay);

                //clear the background of today
                ViewGroup dayOfMonthContainer;
                dayOfMonthContainer = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + CurrentDayIndex);
                dayOfMonthContainer.setBackgroundResource(0);

                //as default sets the today day of month as selected day
                ViewGroup dayOfMonthContainer1;
                dayOfMonthContainer1 = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + (curentmontOffset + IrDay));
                dayOfMonthContainer1.setBackgroundResource(R.drawable.circle);

                ViewGroup dayOfMonthContainer2;
                dayOfMonthContainer2 = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + LastSelectedDayIndex);
                dayOfMonthContainer2.setBackgroundResource(0);

                lastMonthOffset = curentmontOffset;

                LastSelectedDayIndex = curentmontOffset + IrDay;


                // Toast.makeText(mContext, mCalendarFarsi.getIranianDate(), Toast.LENGTH_SHORT).show();
                //  textView2.setText(mCalendarFarsi.getIranianDate());
                mCalendar.set(mCalendarFarsi.getGregorianYear(), mCalendarFarsi.getGregorianMonth() - 1, mCalendarFarsi.getGregorianDay());
                ummalquraCalendaruCal.setTime(mCalendar.getTime());
                // mCalendarFarsi.addIr(CalendarFarsi.MONTH,mCurrentMonthIndex);

                refreshCalendar(mCalendar);
                if (mOnMonthChangedListener != null) {
                    mOnMonthChangedListener.onMonthChanged(mCalendar.getTime(), mCalendarFarsi);
                }

            }
        });

        mNextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mCurrentMonthIndex++;
                mCalendar = Calendar.getInstance(Locale.getDefault());
                mCalendarFarsi = new CalendarFarsi(mCalendar);
                int IrMonth = mCalendarFarsi.getIranianMonth();
                int IrDay = mCalendarFarsi.getIranianDay();
                int addyearIr = ((mCurrentMonthIndex + IrMonth));

                if (addyearIr > 0) {
                    mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() + (addyearIr / 12), (addyearIr % 12), IrDay);
                } else if (addyearIr < 0) {
                    mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() - ((Math.abs(addyearIr) / 12) + 1), 12 - (Math.abs(addyearIr) % 12), IrDay);
                } else if (addyearIr == 0) {
                    mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() - ((addyearIr / 12) + 1), 12, IrDay);
                }

                if (LastSelectedDayIndex != 0) {
                    ViewGroup dayOfMonthContainer;
                    dayOfMonthContainer = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + LastSelectedDayIndex);
                    dayOfMonthContainer.setBackgroundResource(0);
                }
                int curentmontOffset = CalendarFarsiUtility.getMonthOffset(mCalendarFarsi, mFirstDayOfWeek);
                mCalendarFarsi.setIr(CalendarFarsi.DAY_OF_MONTH, IrDay);

                //clear the background of today
                ViewGroup dayOfMonthContainer;
                dayOfMonthContainer = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + CurrentDayIndex);
                dayOfMonthContainer.setBackgroundResource(0);

                //as default sets the today day of month as selected day
                ViewGroup dayOfMonthContainer1;
                dayOfMonthContainer1 = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + (curentmontOffset + IrDay));
                dayOfMonthContainer1.setBackgroundResource(R.drawable.circle);

                ViewGroup dayOfMonthContainer2;
                dayOfMonthContainer2 = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + LastSelectedDayIndex);
                dayOfMonthContainer2.setBackgroundResource(0);

                lastMonthOffset = curentmontOffset;

                LastSelectedDayIndex = curentmontOffset + IrDay;
                mCalendar.set(mCalendarFarsi.getGregorianYear(), mCalendarFarsi.getGregorianMonth() - 1, mCalendarFarsi.getGregorianDay());
                ummalquraCalendaruCal.setTime(mCalendar.getTime());


                refreshCalendar(mCalendar);
                if (mOnMonthChangedListener != null) {
                    mOnMonthChangedListener.onMonthChanged(mCalendar.getTime(), mCalendarFarsi);
                }

            }
        });

        // setFirstDayOfWeek(Calendar.SUNDAY);
        // refreshCalendar(Calendar.getInstance(getLocale()));
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_SUPER_STATE, super.onSaveInstanceState());
        bundle.putInt(IRANIAN_YEAR, mCalendarFarsi.getIranianYear());
        bundle.putInt(IRANIAN_MONTH, mCalendarFarsi.getIranianMonth());
        bundle.putInt(IRANIAN_DAY, mCalendarFarsi.getIranianDay());
        bundle.putInt(GREGORIAN_YEAR, mCalendarFarsi.getGregorianYear());
        bundle.putInt(GREGORIAN_MONTH, mCalendarFarsi.getGregorianMonth());
        bundle.putInt(GREGORIAN_DAY, mCalendarFarsi.getGregorianDay());
        bundle.putInt(CURRENT_MONTH_INDEX, mCurrentMonthIndex);
        return bundle;
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCalendarFarsi.setIranianDate(bundle.getInt(IRANIAN_YEAR), bundle.getInt(IRANIAN_MONTH), bundle.getInt(IRANIAN_DAY));
            mCalendar.set(bundle.getInt(GREGORIAN_YEAR), bundle.getInt(GREGORIAN_MONTH) - 1, bundle.getInt(GREGORIAN_DAY));
            mCurrentMonthIndex = bundle.getInt(CURRENT_MONTH_INDEX);
            refreshCalendar(mCalendar);
            super.onRestoreInstanceState(bundle.getParcelable(KEY_SUPER_STATE));
        } else
            super.onRestoreInstanceState(state);
    }

    /**
     * Display calendar title with next previous month button
     */
    private void initTitleLayout() {
        View titleLayout = mView.findViewById(R.id.title_layout);
        titleLayout.setBackgroundColor(mCalendarTitleBackgroundColor);
        Typeface myTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/B Nazanin Bold.ttf");
        Typeface newRoman = Typeface.createFromAsset(mContext.getAssets(), "fonts/Time Roman.ttf");
        Typeface BNAZANIN = Typeface.createFromAsset(mContext.getAssets(), "fonts/BNAZANIN.TTF");
        String hijriDate = getHijridate();

        TextView dateTitle = (TextView) mView.findViewById(R.id.dateTitle);
        TextView IranianDate_tv = (TextView) mView.findViewById(R.id.IranianDate_tv);
        TextView day_of_month_et = (TextView) mView.findViewById(R.id.day_of_month_et);
        TextView Hijri_date_tv = (TextView) mView.findViewById(R.id.Hijri_date_tv);

        dateTitle.setTypeface(newRoman);
        Hijri_date_tv.setTypeface(BNAZANIN, Typeface.BOLD);
        day_of_month_et.setTypeface(myTypeface);
        IranianDate_tv.setTypeface(myTypeface);


        Hijri_date_tv.setText(hijriDate);
        day_of_month_et.setText(String.valueOf(mCalendarFarsi.getIranianDay()));
        String dateText = CalendarUtility.getCurrentMonth(mCurrentMonthIndex).toUpperCase(Locale.getDefault()) + " " + getCurrentYear();
        IranianDate_tv.setText(mCalendarFarsi.getIraniandate());
        dateTitle.setText(mCalendarFarsi.getGregoriandate());
        dateTitle.setTextColor(mCalendarTitleTextColor);

        if (null != getTypeface()) {
            dateTitle.setTypeface(getTypeface(), Typeface.BOLD);

        }



        Hijri_date_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnMonthTitleClickListener != null) {
                    mOnHijriMonthTitleClickListener.onHijriMonthTitleClick(mCalendar.getTime());

                }
            }
        });

        IranianDate_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mOnMonthTitleClickListener != null) {
                    mOnMonthTitleClickListener.onMonthTitleClick(mCalendar.getTime());

                }
            }
        });

        dateTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (mOnGregorianMonthTitleClickListener != null) {
                    mOnGregorianMonthTitleClickListener.onGregorianMonthTitleClick(mCalendar.getTime());

                }
            }
        });
    }

    public void resfreshweekdaysname() {
        ImageView dayOfWeek;
        for (int i = 1; i <= 7; i++) {
            dayOfWeek = (ImageView) mView.findViewWithTag(mContext.getString(R.string.day_of_week) + i);
            dayOfWeek.setColorFilter(ContextCompat.getColor(mContext, R.color.commonDayOfWeekColor), PorterDuff.Mode.SRC_ATOP);
        }
    }

    /**
     * Initialize the calendar week layout, considers start day
     */
    private void initWeekLayout() {

        ImageView dayOfWeek;
        String dayOfTheWeekString;
//        Log.v("this is INITWEEKEND","FIRST DAY METHODE= "+mCalendar.getFirstDayOfWeek());
        Log.v("this is INITWEEKEND", "mFirstDayOfWeek= " + mFirstDayOfWeek);
        //Setting background color white
        View titleLayout = mView.findViewById(R.id.week_layout);
        titleLayout.setBackgroundColor(mWeekLayoutBackgroundColor);

        final String[] weekDaysArray = new DateFormatSymbols(getLocale()).getShortWeekdays();

        for (int i = 1; i < weekDaysArray.length; i++) {
            dayOfTheWeekString = weekDaysArray[i];
            Log.v("this is number", "number= " + CalendarUtility.getWeekIndex(i, mCalendar));
            int length = dayOfTheWeekString.length() < 3 ? dayOfTheWeekString.length() : 3;
            dayOfTheWeekString = dayOfTheWeekString.substring(0, length).toUpperCase();
            dayOfWeek = (ImageView) mView.findViewWithTag(mContext.getString(R.string.day_of_week) + CalendarUtility.getWeekIndex(i, mCalendar));
            dayOfWeek.setImageResource(setWeekDayName(dayOfTheWeekString));
            mIsCommonDay = true;
            int totalDayOfWeekend[] = totalDayOfWeekend();
            Log.v("this is before", "i= " + i);
            if (totalDayOfWeekend.length == 1) {


                for (int weekend : totalDayOfWeekend) {
                    if (i == weekend) {
                        Log.v("this is inside if", "i= " + i);

                        dayOfWeek.setColorFilter(ContextCompat.getColor(mContext, R.color.Red), PorterDuff.Mode.SRC_ATOP);
                        mIsCommonDay = false;
                    }
                    Log.v("this is inside for", "i= " + i);
                }
            }
            Log.v("this is after for", "i= " + i);
            if (mIsCommonDay) {
                // dayOfWeek.setTextColor(mDayOfWeekTextColor);
                dayOfWeek.setColorFilter(Color.BLACK);
            }

            if (null != getTypeface()) {
                //  dayOfWeek.setTypeface(getTypeface());
            }
        }
    }

    /**
     * Date Picker (Month & Year only)
     *
     * @param context
     * @author chris.chen
     */
    private void createDialogWithoutDateField(Context context) {

        mCalendar = Calendar.getInstance(Locale.getDefault());
        final int iYear = mCalendar.get(Calendar.YEAR);
        final int iMonth = mCalendar.get(Calendar.MONTH);
        final int iDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(context, R.style.CalendarViewTitle, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(
                    DatePicker datePicker,
                    int year,
                    int monthOfYear,
                    int dayOfMonth
            ) {

                int diffMonth = (year - iYear) * 12 + (monthOfYear - iMonth);

                mCurrentMonthIndex = diffMonth;
                mCalendar.add(Calendar.MONTH, mCurrentMonthIndex);

                refreshCalendar(mCalendar);
                if (mOnMonthChangedListener != null) {
                    mOnMonthChangedListener.onMonthChanged(mCalendar.getTime(), mCalendarFarsi);
                }

            }
        }, iYear,
                iMonth,
                iDay);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
            if (daySpinnerId != 0) {
                View daySpinner = dpd.getDatePicker().findViewById(daySpinnerId);
                if (daySpinner != null) {
                    daySpinner.setVisibility(View.GONE);
                }
            }

            int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
            if (monthSpinnerId != 0) {
                View monthSpinner = dpd.getDatePicker().findViewById(monthSpinnerId);
                if (monthSpinner != null) {
                    monthSpinner.setVisibility(View.VISIBLE);
                }
            }

            int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
            if (yearSpinnerId != 0) {
                View yearSpinner = dpd.getDatePicker().findViewById(yearSpinnerId);
                if (yearSpinner != null) {
                    yearSpinner.setVisibility(View.VISIBLE);
                }
            }
        } else { //Older SDK versions
            java.lang.reflect.Field f[] = dpd.getDatePicker().getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : f) {
                if (field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner")) {
                    field.setAccessible(true);
                    Object dayPicker = null;
                    try {
                        dayPicker = field.get(dpd.getDatePicker());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) dayPicker).setVisibility(View.GONE);
                }

                if (field.getName().equals("mMonthPicker") || field.getName().equals("mMonthSpinner")) {
                    field.setAccessible(true);
                    Object monthPicker = null;
                    try {
                        monthPicker = field.get(dpd.getDatePicker());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) monthPicker).setVisibility(View.VISIBLE);
                }

                if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")) {
                    field.setAccessible(true);
                    Object yearPicker = null;
                    try {
                        yearPicker = field.get(dpd.getDatePicker());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    ((View) yearPicker).setVisibility(View.VISIBLE);
                }
            }
        }

        dpd.show();
    }

    /**
     * This method prepare and populate the days in the CalendarView
     */
    private void setDaysInCalendar() {
        Calendar calendar;

        //NEW METHOD FOR SETTING DAYS IN CALENDAR

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(mCalendarFarsi.getGregorianYear(), mCalendarFarsi.getGregorianMonth() - 1, mCalendarFarsi.getGregorianDay());
        CalendarFarsi calendarFarsi = new CalendarFarsi(calendar1);
        Log.v("INSIDE SE DAYS BEFORE F", "CalendarFarsi Iranian date=" + calendarFarsi.getIranianDate());

        int actualMaximum = calendarFarsi.getActualMaximum(calendar1);
        calendarFarsi.setIr(CalendarFarsi.DAY_OF_MONTH, 1);
        Log.v("INSIDE SE DAYS BEFORE F", "CalendarFarsi Iranian date=" + calendarFarsi.getIranianDate());
        Log.v("INSIDE SE DAYS BEFORE F", "CalendarFarsi Iranian Day of Week=" + calendarFarsi.getDayOfWeek());
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendarFarsi.getGregorianYear(), calendarFarsi.getGregorianMonth() - 1, calendarFarsi.getGregorianDay());
        int MonthOffSet = CalendarFarsiUtility.getMonthOffset(mContext, calendar2, mFirstDayOfWeek);
        calendarFarsi.add(CalendarFarsi.DATE, -MonthOffSet);
        calendar2.add(Calendar.DATE, -MonthOffSet);
        int monthEndIndex = 42 - (actualMaximum + MonthOffSet);

        Log.v("INSIDE SE DAYS BEFORE F", "mCalendarFarsi Iranian date=" + mCalendarFarsi.getIranianDate());


        DayView dayView;
        ViewGroup dayOfMonthContainer;
        for (int i = 1; i < 43; i++) {
            Log.v("INSIDE FOR", "IRANIAN DATE=" + calendarFarsi.getIranianDate());
            dayOfMonthContainer = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + i);
            dayView = (DayView) mView.findViewWithTag(mContext.getString(R.string.day_of_month_text) + i);
            if (dayView == null)
                continue;

            //Apply the default styles
            dayOfMonthContainer.setOnClickListener(null);
            dayView.bindIr(calendarFarsi);
            dayView.setVisibility(View.VISIBLE);

            if (null != getTypeface()) {
                // dayView.setTypeface(getTypeface());
            }

            if (CalendarFarsiUtility.isSameMonth(mCalendarFarsi, calendarFarsi.getIranianYear(), calendarFarsi.getIranianMonth())) {
                dayOfMonthContainer.setOnClickListener(onDayOfMonthClickListener);
                dayOfMonthContainer.setOnLongClickListener(onDayOfMonthLongClickListener);
                dayView.setBackgroundColor(mCalendarBackgroundColor);
                mIsCommonDay = true;
                if (totalDayOfWeekend().length != 0) {
                    for (int weekend : totalDayOfWeekend()) {
                        if (calendar2.get(Calendar.DAY_OF_WEEK) == weekend) {
                            // dayView.setTextColor(mWeekendColor);
                            dayView.setColorFilter(Color.RED);
                            mIsCommonDay = false;
                        }
                    }
                }

                if (mIsCommonDay) {
                    //  dayView.setTextColor(mDayOfWeekTextColor);
                    dayView.setColorFilter(Color.BLACK);
                }
            } else {
                dayView.setBackgroundColor(mDisabledDayBackgroundColor);
                // dayView.setTextColor(mDisabledDayTextColor);

                if (!isOverflowDateVisible()) {
                    dayView.setVisibility(View.INVISIBLE);
                    Log.v("THIS IS SET DAYS IN CAL", "INSIDE IF___ i=" + i);
                    Log.v("THIS IS SET DAYS IN CAL", "IranianMonth= " + String.valueOf(calendarFarsi.getIranianMonth()));
                } else if (i >= 36 && ((float) monthEndIndex / 7.0f) >= 1) {
                    dayView.setVisibility(View.INVISIBLE);
                    Log.v("THIS IS SET DAYS IN CAL", "INSIDE ELSE IF___ i=" + i);
                }
            }
            dayView.decorate();

            //Set the current day color
            if (CalendarFarsiUtility.isSameMonth(mCalendarFarsi, calendarFarsi.getIranianYear(), calendarFarsi.getIranianMonth()))
                setCurrentDay(calendar2.getTime(), i);
            calendarFarsi.add(CalendarFarsi.DATE, 1);
            calendar2.add(Calendar.DATE, 1);
            //dayOfMonthIndex++;
        }

        // If the last week row has no visible days, hide it or show it in case
        ViewGroup weekRow = (ViewGroup) mView.findViewWithTag("weekRow6");
        dayView = (DayView) mView.findViewWithTag("dayOfMonthText36");
        if (dayView.getVisibility() != VISIBLE) {
            weekRow.setVisibility(GONE);
        } else {
            weekRow.setVisibility(VISIBLE);
        }
    }

    private void clearDayOfTheMonthStyle(Date currentDate) {
        if (currentDate != null) {
            final Calendar calendar = CalendarUtility.getTodayCalendar(mContext, mFirstDayOfWeek);
            calendar.setFirstDayOfWeek(mFirstDayOfWeek);
            calendar.setTime(currentDate);

            final DayView dayView = findViewByCalendar(calendar);
            dayView.setBackgroundColor(mCalendarBackgroundColor);
            mIsCommonDay = true;
            if (totalDayOfWeekend().length != 0) {
                for (int weekend : totalDayOfWeekend()) {
                    if (calendar.get(Calendar.DAY_OF_WEEK) == weekend) {
                        // dayView.setTextColor(mWeekendColor);
                        mIsCommonDay = false;
                    }
                }
            }

            if (mIsCommonDay) {
                // dayView.setTextColor(mDayOfWeekTextColor);
            }
        }
    }

    public DayView findViewByDate(@NonNull Date dateToFind) {
        final Calendar calendar = Calendar.getInstance(getLocale());
        calendar.setTime(dateToFind);
        return (DayView) getView(mContext.getString(R.string.day_of_month_text), calendar);
    }

    private DayView findViewByCalendar(@NonNull Calendar calendarToFind) {
        return (DayView) getView(mContext.getString(R.string.day_of_month_text), calendarToFind);
    }

    private int getDayIndexByDate(Calendar calendar) {
        int monthOffset = CalendarUtility.getMonthOffset(calendar, mFirstDayOfWeek);
        int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

        return currentDay + monthOffset;
    }

    private View getView(String key, Calendar currentCalendar) {
        final int index = getDayIndexByDate(currentCalendar);
        return mView.findViewWithTag(key + index);
    }

    public void refreshCalendar(Calendar calendar) {
        mCalendar = calendar;

        mCalendar.setFirstDayOfWeek(mFirstDayOfWeek);
        Log.v("this is REFRESH", "I AM REFRESH");
        initTitleLayout();
        setTotalDayOfWeekend();
        initWeekLayout();

        setDaysInCalendar();
    }

    private void setTotalDayOfWeekend() {
        int[] weekendDay = new int[Integer.bitCount(mWeekend)];
        char days[] = Integer.toBinaryString(mWeekend).toCharArray();
        int day = 1;
        int index = 0;
        for (int i = days.length - 1; i >= 0; i--) {
            if (days[i] == '1') {
                weekendDay[index] = day;
                index++;
            }
            day++;
        }

        mTotalDayOfWeekend = weekendDay;
    }

    private int[] totalDayOfWeekend() {
        return mTotalDayOfWeekend;
    }

    public void setCurrentDay(@NonNull Date todayDate, int i) {
        final Calendar calendar = Calendar.getInstance(getLocale());
        calendar.setTime(todayDate);
        Log.v("THIS IS setCurrentDay", " Sended i =" + String.valueOf(i));
        if (CalendarUtility.isToday(calendar)) {
            CurrentDayIndex = i;
            LastSelectedDayIndex = i;
            final DayView dayOfMonth = (DayView) mView.findViewWithTag(mContext.getString(R.string.day_of_month_text) + i);
            ViewGroup dayOfMonthContainer;
            dayOfMonthContainer = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + i);
            Log.v("THIS IS setCurrentDay", "Accepted i =" + String.valueOf(i));
            //dayOfMonth.setTextColor(mCurrentDayOfMonth);

            dayOfMonthContainer.setBackgroundResource(R.drawable.current_day_bg);
        }
    }

    public void setDateAsSelected(int INDEX, int monthOffset) {
        DayView dayView;
        ViewGroup dayOfMonthContainer;
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar;
        calendar = mCalendar;
        CalendarFarsi c = new CalendarFarsi(calendar1);
        calendar.add(Calendar.DAY_OF_MONTH, -(INDEX - LastSelectedDayIndex));
        Log.v("setDateAsSelected", "before if_____mcalendarfarsi_iraniandate= " + mCalendarFarsi.getIranianDate());
        Log.v("setDateAsSelected", "before if_____c_iraniandate= " + c.getIranianDate());
        ViewGroup dayOfMonthContainer1;
        if (!(CalendarFarsiUtility.isToday(mCalendarFarsi))) {
            Log.v("setDateAsSelected", "outer if_____LastSelectedDayIndex= " + LastSelectedDayIndex);

            dayOfMonthContainer = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + INDEX);
            dayOfMonthContainer.setBackgroundResource(R.drawable.circle);
        }
        if (!CalendarUtility.isSameDay(calendar1, calendar)) {
            dayOfMonthContainer1 = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + LastSelectedDayIndex);
            dayOfMonthContainer1.setBackgroundResource(0);
            Log.v("setDateAsSelected", "inBoth if_____LastSelectedDayIndex= " + LastSelectedDayIndex);
        }

        Log.v("setDateAsSelected", "outBoth if_____LastSelectedDayIndex= " + LastSelectedDayIndex);
        LastSelectedDayIndex = INDEX;
        lastSelectedDayMonthOffset = monthOffset;
        initTitleLayout();
    }


    public void setDateAsSelected(Date currentDate) {
        final Calendar currentCalendar = CalendarUtility.getTodayCalendar(mContext, mFirstDayOfWeek);
        currentCalendar.setFirstDayOfWeek(mFirstDayOfWeek);
        currentCalendar.setTime(currentDate);

        // Clear previous marks
        clearDayOfTheMonthStyle(mLastSelectedDay);

        // Store current values as last values
        setLastSelectedDay(currentDate);

        // Mark current day as selected
        DayView view = findViewByCalendar(currentCalendar);
        view.setBackgroundColor(mSelectedDayBackground);
        // view.setTextColor(mSelectedDayTextColor);
    }

    private OnLongClickListener onDayOfMonthLongClickListener = new OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            // Extract day selected
            ViewGroup dayOfMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfMonthContainer.getTag();
            tagId = tagId.substring(mContext.getString(R.string.day_of_month_container).length(), tagId.length());
            final ImageView dayOfMonthText = (ImageView) view.findViewWithTag(mContext.getString(R.string.day_of_month_text) + tagId);

            // Fire event
            final Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(mFirstDayOfWeek);
            calendar.setTime(mCalendar.getTime());
            int monthOffset = CalendarFarsiUtility.getMonthOffset(mContext, calendar, mFirstDayOfWeek);
            CalendarFarsi calendarFarsi = new CalendarFarsi(calendar);
            int dayofmonthIr = Integer.parseInt(tagId) - monthOffset;
            // calendarFarsi.setIranianDate(calendarFarsi.getIranianYear(),calendarFarsi.getIranianMonth(),dayofmonthIr);
            calendarFarsi.setIr(CalendarFarsi.DAY_OF_MONTH, dayofmonthIr);

            int dayOfMonth = calendarFarsi.getGregorianDay();
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setDateAsSelected(dayofmonthIr + monthOffset, monthOffset);

            //Toast.makeText(mContext, String.valueOf(dayOfMonth), Toast.LENGTH_SHORT).show();
            //  Toast.makeText(mContext, calendarFarsi.getIranianDate(), Toast.LENGTH_SHORT).show();
            //Set the current day color
            setCurrentDay(mCalendar.getTime(), 2000);

            if (mOnDateLongClickListener != null) {
                mOnDateLongClickListener.onDateLongClick(calendar.getTime());
            }

            return false;
        }
    };

    public String getHijridate() {

        return (ummalquraCalendaruCal.get(Calendar.DAY_OF_MONTH) + " " + getHijriMonth() + " " + ummalquraCalendaruCal.get(Calendar.YEAR));
    }

    public String getHijriMonth() {
        String hijriMONTH = ummalquraCalendaruCal.getDisplayName(Calendar.MONTH, Calendar.SHORT, getLocale());

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

    private OnClickListener onDayOfMonthClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // Extract day selected
            ViewGroup dayOfMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfMonthContainer.getTag();
            tagId = tagId.substring(mContext.getString(R.string.day_of_month_container).length(), tagId.length());
            final ImageView dayOfMonthText = (ImageView) view.findViewWithTag(mContext.getString(R.string.day_of_month_text) + tagId);

            // Fire event
            final Calendar calendar = Calendar.getInstance();
            calendar.setFirstDayOfWeek(mFirstDayOfWeek);
            calendar.setTime(mCalendar.getTime());
            int monthOffset = CalendarFarsiUtility.getMonthOffset(mContext, calendar, mFirstDayOfWeek);
            CalendarFarsi calendarFarsi = new CalendarFarsi(calendar);
            int selecteddayindex = Integer.parseInt(tagId);

            int dayofmonthIr = selecteddayindex - monthOffset;
            calendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear(), mCalendarFarsi.getIranianMonth(), dayofmonthIr);

            mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear(), mCalendarFarsi.getIranianMonth(), dayofmonthIr);
            mCalendar.set(mCalendarFarsi.getGregorianYear(), mCalendarFarsi.getGregorianMonth() - 1, mCalendarFarsi.getGregorianDay());
            mgregorianCalendar.set(mCalendarFarsi.getGregorianYear(), mCalendarFarsi.getGregorianMonth() - 1, mCalendarFarsi.getGregorianDay());
            ummalquraCalendaruCal.setTime(mgregorianCalendar.getTime());
            // calendarFarsi.setIranianDate(calendarFarsi.getIranianYear(),calendarFarsi.getIranianMonth(),dayofmonthIr);
            // calendarFarsi.setIr(CalendarFarsi.DAY_OF_MONTH,dayofmonthIr);
            // Toast.makeText(mContext,String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH)), Toast.LENGTH_SHORT).show();
            int dayOfMonth = calendarFarsi.getGregorianDay();
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            setDateAsSelected(selecteddayindex, monthOffset);
            // Toast.makeText(mContext, calendarFarsi.getGregorianDate(), Toast.LENGTH_SHORT).show();

            //Set the current day color
            // setCurrentDay(mCalendar.getTime(),1000);

            if (mOnDateClickListener != null) {
                mOnDateClickListener.onDateClick(calendar.getTime(), mCalendarFarsi);
            }
        }
    };

    private boolean isGutterDrag(float x, float dx) {
        return (x < mDefaultGutterSize && dx > 0) || (x > getWidth() - mDefaultGutterSize && dx < 0);
    }

    private void setScrollingCacheEnabled(boolean enabled) {
        if (mScrollingCacheEnabled != enabled) {
            mScrollingCacheEnabled = enabled;
            if (USE_CACHE) {
                final int size = getChildCount();
                for (int i = 0; i < size; ++i) {
                    final View child = getChildAt(i);
                    if (child.getVisibility() != GONE) {
                        child.setDrawingCacheEnabled(enabled);
                    }
                }
            }
        }
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
            if (mVelocityTracker != null) {
                mVelocityTracker.clear();
            }
        }
    }

    private void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }

        mScrollState = newState;
    }

    private void requestParentDisallowInterceptTouchEvent(boolean disallowIntercept) {
        final ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(disallowIntercept);
        }
    }

    /**
     * Tests scroll ability within child views of v given a delta of dx.
     *
     * @param v      View to test for horizontal scroll ability
     * @param checkV Whether the view v passed should itself be checked for scrollability (true),
     *               or just its children (false).
     * @param dx     Delta scrolled in pixels
     * @param x      X coordinate of the active touch point
     * @param y      Y coordinate of the active touch point
     * @return true if child views of v can be scrolled by delta of dx.
     */
    protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
        if (v instanceof ViewGroup) {
            final ViewGroup group = (ViewGroup) v;
            final int scrollX = v.getScrollX();
            final int scrollY = v.getScrollY();
            final int count = group.getChildCount();
            // Count backwards - let topmost views consume scroll distance first.
            for (int i = count - 1; i >= 0; i--) {
                // This will not work for transformed views in Honeycomb+
                final View child = group.getChildAt(i);
                if (x + scrollX >= child.getLeft() && x + scrollX < child.getRight() &&
                        y + scrollY >= child.getTop() && y + scrollY < child.getBottom() &&
                        canScroll(child, true, dx, x + scrollX - child.getLeft(),
                                y + scrollY - child.getTop())) {
                    return true;
                }
            }
        }

        return checkV && ViewCompat.canScrollHorizontally(v, -dx);
    }

    private void completeScroll(boolean postEvents) {
        boolean needPopulate = mScrollState == SCROLL_STATE_SETTLING;
        if (needPopulate) {
            // Done with scroll, no longer want to cache view drawing.
            setScrollingCacheEnabled(false);
            mScroller.abortAnimation();
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = mScroller.getCurrX();
            int y = mScroller.getCurrY();
            if (oldX != x || oldY != y) {
                scrollTo(x, y);
            }
        }

        if (needPopulate) {
            if (postEvents) {
                ViewCompat.postOnAnimation(this, mEndScrollRunnable);
            } else {
                mEndScrollRunnable.run();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (null != mGestureDetector) {
            mGestureDetector.onTouchEvent(ev);
            super.dispatchTouchEvent(ev);
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        /*
         * This method JUST determines whether we want to intercept the motion.
         * If we return true, onMotionEvent will be called and we do the actual
         * scrolling there.
         */

        final int action = ev.getAction() & MotionEventCompat.ACTION_MASK;

        // Always take care of the touch gesture being complete.
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            // Release the drag.
            mIsBeingDragged = false;
            mIsUnableToDrag = false;
            mActivePointerId = INVALID_POINTER;
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
            return false;
        }

        // Nothing more to do here if we have decided whether or not we
        // are dragging.
        if (action != MotionEvent.ACTION_DOWN) {
            if (mIsBeingDragged) {
                return true;
            }
            if (mIsUnableToDrag) {
                return false;
            }
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                /*
                 * mIsBeingDragged == false, otherwise the shortcut would have caught it. Check
                 * whether the user has moved far enough from his original down touch.
                 */

                /*
                * Locally do absolute value. mLastMotionY is set to the y value
                * of the down event.
                */
                final int activePointerId = mActivePointerId;
                if (activePointerId == INVALID_POINTER) {
                    // If we don't have a valid id, the touch down wasn't on content.
                    break;
                }

                final int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
                final float x = MotionEventCompat.getX(ev, pointerIndex);
                final float dx = x - mLastMotionX;
                final float xDiff = Math.abs(dx);
                final float y = MotionEventCompat.getY(ev, pointerIndex);
                final float yDiff = Math.abs(y - mInitialMotionY);

                if (dx != 0 && !isGutterDrag(mLastMotionX, dx) &&
                        canScroll(this, false, (int) dx, (int) x, (int) y)) {
                    // Nested view has scrollable area under this point. Let it be handled there.
                    mLastMotionX = x;
                    mLastMotionY = y;
                    mIsUnableToDrag = true;
                    return false;
                }
                if (xDiff > mTouchSlop && xDiff * 0.5f > yDiff) {
                    mIsBeingDragged = true;
                    requestParentDisallowInterceptTouchEvent(true);
                    setScrollState(SCROLL_STATE_DRAGGING);
                    mLastMotionX = dx > 0 ? mInitialMotionX + mTouchSlop :
                            mInitialMotionX - mTouchSlop;
                    mLastMotionY = y;
                    setScrollingCacheEnabled(true);
                } else if (yDiff > mTouchSlop) {
                    // The finger has moved enough in the vertical
                    // direction to be counted as a drag...  abort
                    // any attempt to drag horizontally, to work correctly
                    // with children that have scrolling containers.
                    mIsUnableToDrag = true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                /*
                 * Remember location of down touch.
                 * ACTION_DOWN always refers to pointer index 0.
                 */
                mLastMotionX = mInitialMotionX = ev.getX();
                mLastMotionY = mInitialMotionY = ev.getY();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsUnableToDrag = false;

                mScroller.computeScrollOffset();
                if (mScrollState == SCROLL_STATE_SETTLING &&
                        Math.abs(mScroller.getFinalX() - mScroller.getCurrX()) > mCloseEnough) {
                    mIsBeingDragged = true;
                    requestParentDisallowInterceptTouchEvent(true);
                    setScrollState(SCROLL_STATE_DRAGGING);
                } else {
                    completeScroll(false);
                    mIsBeingDragged = false;
                }
                break;
            }

            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }

        mVelocityTracker.addMovement(ev);

        /*
         * The only time we want to intercept motion events is if we are in the
         * drag mode.
         */
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public int setWeekDayName(String weekDayName) {
        int id;
        switch (weekDayName) {
            case "SAT":
                id = R.drawable.shanbeh;
                break;
            case "SUN":
                id = R.drawable.yek_shanbeh;
                break;
            case "MON":
                id = R.drawable.do_shanbeh;
                break;
            case "TUE":
                id = R.drawable.seh_shanbeh;
                break;
            case "WED":
                id = R.drawable.chahar_shanbeh;
                break;
            case "THU":
                id = R.drawable.panj_shanbeh;
                break;
            case "FRI":
                id = R.drawable.jomeh;
                break;
            default:
                id = 0;
        }
        return id;
    }

    /**
     * CalendarGestureDetector class used to detect Swipes gestures.
     *
     * @author jonatan.salas
     */
    public class CalendarGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > mTouchSlop && Math.abs(velocityX) > mMinimumVelocity && Math.abs(velocityX) < mMaximumVelocity) {
                        if (e2.getX() - e1.getX() > mFlingDistance) {
                            mCurrentMonthIndex--;
                            mCalendar = Calendar.getInstance(Locale.getDefault());
                            mCalendarFarsi = new CalendarFarsi(mCalendar);
                            int IrMonth = mCalendarFarsi.getIranianMonth();
                            int IrDay = mCalendarFarsi.getIranianDay();
                            int addyearIr = ((mCurrentMonthIndex + IrMonth));
                            if (addyearIr > 0) {
                                mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() + (addyearIr / 12), (addyearIr % 12), mCalendarFarsi.getIranianDay());
                            } else if (addyearIr < 0) {
                                mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() - ((Math.abs(addyearIr) / 12) + 1), 12 - (Math.abs(addyearIr) % 12), mCalendarFarsi.getIranianDay());
                            } else if (addyearIr == 0) {
                                mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() - ((addyearIr / 12) + 1), 12, IrDay);
                            }
                            int curentmontOffset = CalendarFarsiUtility.getMonthOffset(mCalendarFarsi, mFirstDayOfWeek);
                            mCalendarFarsi.setIr(CalendarFarsi.DAY_OF_MONTH, IrDay);

                            ViewGroup dayOfMonthContainer;
                            dayOfMonthContainer = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + CurrentDayIndex);
                            dayOfMonthContainer.setBackgroundResource(0);

                            //as default sets the today day of month as selected day
                            ViewGroup dayOfMonthContainer1;
                            dayOfMonthContainer1 = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + (curentmontOffset + IrDay));
                            dayOfMonthContainer1.setBackgroundResource(R.drawable.circle);

                            ViewGroup dayOfMonthContainer2;
                            dayOfMonthContainer2 = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + LastSelectedDayIndex);
                            dayOfMonthContainer2.setBackgroundResource(0);

                            lastMonthOffset = curentmontOffset;

                            LastSelectedDayIndex = curentmontOffset + IrDay;


                            mCalendar.set(mCalendarFarsi.getGregorianYear(), mCalendarFarsi.getGregorianMonth() - 1, mCalendarFarsi.getGregorianDay());
                            ummalquraCalendaruCal.setTime(mCalendar.getTime());
                            // mCalendarFarsi.addIr(CalendarFarsi.MONTH,mCurrentMonthIndex);

                            refreshCalendar(mCalendar);
                            if (mOnMonthChangedListener != null) {
                                mOnMonthChangedListener.onMonthChanged(mCalendar.getTime(), mCalendarFarsi);
                            }

                        } else if (e1.getX() - e2.getX() > mFlingDistance) {
                            mCurrentMonthIndex++;
                            mCalendar = Calendar.getInstance(Locale.getDefault());
                            mCalendarFarsi = new CalendarFarsi(mCalendar);
                            int IrMonth = mCalendarFarsi.getIranianMonth();
                            int IrDay = mCalendarFarsi.getIranianDay();
                            int addyearIr = ((mCurrentMonthIndex + IrMonth));
                            if (addyearIr > 0) {
                                mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() + (addyearIr / 12), (addyearIr % 12), mCalendarFarsi.getIranianDay());
                            } else if (addyearIr < 0) {
                                mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() - ((Math.abs(addyearIr) / 12) + 1), 12 - (Math.abs(addyearIr) % 12), mCalendarFarsi.getIranianDay());
                            } else if (addyearIr == 0) {
                                mCalendarFarsi.setIranianDate(mCalendarFarsi.getIranianYear() - ((addyearIr / 12) + 1), 12, IrDay);
                            }

                            int curentmontOffset = CalendarFarsiUtility.getMonthOffset(mCalendarFarsi, mFirstDayOfWeek);
                            mCalendarFarsi.setIr(CalendarFarsi.DAY_OF_MONTH, IrDay);

                            ViewGroup dayOfMonthContainer;
                            dayOfMonthContainer = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + CurrentDayIndex);
                            dayOfMonthContainer.setBackgroundResource(0);

                            //as default sets the today day of month as selected day
                            ViewGroup dayOfMonthContainer1;
                            dayOfMonthContainer1 = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + (curentmontOffset + IrDay));
                            dayOfMonthContainer1.setBackgroundResource(R.drawable.circle);

                            ViewGroup dayOfMonthContainer2;
                            dayOfMonthContainer2 = (ViewGroup) mView.findViewWithTag(mContext.getString(R.string.day_of_month_container) + LastSelectedDayIndex);
                            dayOfMonthContainer2.setBackgroundResource(0);

                            lastMonthOffset = curentmontOffset;

                            LastSelectedDayIndex = curentmontOffset + IrDay;


                            // Toast.makeText(mContext, mCalendarFarsi.getIranianDate(), Toast.LENGTH_SHORT).show();
                            //  textView2.setText(mCalendarFarsi.getIranianDate());
                            mCalendar.set(mCalendarFarsi.getGregorianYear(), mCalendarFarsi.getGregorianMonth() - 1, mCalendarFarsi.getGregorianDay());
                            ummalquraCalendaruCal.setTime(mCalendar.getTime());

                            // mCalendarFarsi.addIr(CalendarFarsi.MONTH,mCurrentMonthIndex);
                            refreshCalendar(mCalendar);

                            if (mOnMonthChangedListener != null) {
                                mOnMonthChangedListener.onMonthChanged(mCalendar.getTime(), mCalendarFarsi);
                            }
                        }
                    }
                }

                return true;

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    /**
     * Interface that define a method to
     * implement to handle a selected date event,
     *
     * @author jonatan.salas
     */
    public interface OnDateClickListener {

        /**
         * Method that lets you handle
         * when a user touches a particular date.
         *
         * @param selectedDate - the date selected by the user.
         */
        void onDateClick(@NonNull Date selectedDate, CalendarFarsi calendarFarsi);
    }

    /**
     * Interface that define a method to
     * implement to handle a selected date event,
     *
     * @author jonatan.salas
     */
    public interface OnDateLongClickListener {

        /**
         * Method that lets you handle
         * when a user touches a particular date.
         *
         * @param selectedDate - the date selected by the user.
         */
        void onDateLongClick(@NonNull Date selectedDate);
    }


    /**
     * Interface that define a method to implement to handle
     * a month changed event.
     *
     * @author jonatan.salas
     */
    public interface OnMonthChangedListener {

        /**
         * Method that lets you handle when a goes to back or next
         * month.
         *
         * @param monthDate - the date with the current month
         */
        void onMonthChanged(@NonNull Date monthDate, CalendarFarsi calendarFarsi);
    }

    /**
     * Interface that define a method to implement to handle
     * a month title change event.
     *
     * @author chris.chen
     */
    public interface OnMonthTitleClickListener {
        void onMonthTitleClick(@NonNull Date monthDate);
    }


    public interface OnHijriMonthTitleClickListener {
        void onHijriMonthTitleClick(@NonNull Date MonthDate);
    }
    public interface OnGregorianMonthTitleClickListener {
        void onGregorianMonthTitleClick(@NonNull Date MonthDate);
    }

    /**
     * Attributes setters and getters.
     */

    public void setOnMonthTitleClickListener(OnMonthTitleClickListener onMonthTitleClickListener) {
        this.mOnMonthTitleClickListener = onMonthTitleClickListener;
    }

    public void setOnHijriMonthTitleClickListener(OnHijriMonthTitleClickListener onHijriMonthTitleClickListener) {
        this.mOnHijriMonthTitleClickListener = onHijriMonthTitleClickListener;
    }
    public void setOnGregorianMonthTitleClickListener(OnGregorianMonthTitleClickListener onGregorianMonthTitleClickListener) {
        this.mOnGregorianMonthTitleClickListener = onGregorianMonthTitleClickListener;
    }

    public void setOnDateClickListener(OnDateClickListener onDateClickListener) {
        this.mOnDateClickListener = onDateClickListener;
    }

    public void setOnDateLongClickListener(OnDateLongClickListener onDateLongClickListener) {
        this.mOnDateLongClickListener = onDateLongClickListener;
    }

    public void setOnMonthChangedListener(OnMonthChangedListener onMonthChangedListener) {
        this.mOnMonthChangedListener = onMonthChangedListener;
    }

    private void setLastSelectedDay(Date lastSelectedDay) {
        this.mLastSelectedDay = lastSelectedDay;
    }

    public void setTypeface(Typeface typeface) {
        this.mTypeface = typeface;
    }

    public void setDecoratorsList(List<DayDecorator> decoratorsList) {
        this.mDecoratorsList = decoratorsList;
    }

    public void setIsOverflowDateVisible(boolean isOverflowDateVisible) {
        this.mIsOverflowDateVisible = isOverflowDateVisible;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.mFirstDayOfWeek = firstDayOfWeek;
        resfreshweekdaysname();
    }

    public void setDisabledDayBackgroundColor(int disabledDayBackgroundColor) {
        this.mDisabledDayBackgroundColor = disabledDayBackgroundColor;
    }

    public void setDisabledDayTextColor(int disabledDayTextColor) {
        this.mDisabledDayTextColor = disabledDayTextColor;
    }

    public void setCalendarBackgroundColor(int calendarBackgroundColor) {
        this.mCalendarBackgroundColor = calendarBackgroundColor;
    }

    public void setSelectedDayBackground(int selectedDayBackground) {
        this.mSelectedDayBackground = selectedDayBackground;
    }

    public void setWeekLayoutBackgroundColor(int weekLayoutBackgroundColor) {
        this.mWeekLayoutBackgroundColor = weekLayoutBackgroundColor;
    }

    public void setCalendarTitleBackgroundColor(int calendarTitleBackgroundColor) {
        this.mCalendarTitleBackgroundColor = calendarTitleBackgroundColor;
    }

    public void setSelectedDayTextColor(int selectedDayTextColor) {
        this.mSelectedDayTextColor = selectedDayTextColor;
    }

    public void setCalendarTitleTextColor(int calendarTitleTextColor) {
        this.mCalendarTitleTextColor = calendarTitleTextColor;
    }

    public void setDayOfWeekTextColor(int dayOfWeekTextColor) {
        this.mDayOfWeekTextColor = dayOfWeekTextColor;
    }

    public void setCurrentDayOfMonth(int currentDayOfMonth) {
        this.mCurrentDayOfMonth = currentDayOfMonth;
    }

    public int getCurrentDayOfMonth() {
        return this.mCurrentDayOfMonth;
    }

    public void setWeekendColor(int weekendColor) {
        this.mWeekendColor = weekendColor;
    }

    public void setWeekend(int weekend) {
        this.mWeekend = weekend;
    }

    public void setBackButtonColor(@ColorRes int colorId) {
        this.mBackButton.setColorFilter(ContextCompat.getColor(mContext, colorId), PorterDuff.Mode.SRC_ATOP);
    }

    public void setNextButtonColor(@ColorRes int colorId) {
        this.mNextButton.setColorFilter(ContextCompat.getColor(mContext, colorId), PorterDuff.Mode.SRC_ATOP);
    }

    public void setBackButtonDrawable(@DrawableRes int drawableId) {
        this.mBackButton.setImageDrawable(ContextCompat.getDrawable(mContext, drawableId));
    }

    public void setNextButtonDrawable(@DrawableRes int drawableId) {
        this.mNextButton.setImageDrawable(ContextCompat.getDrawable(mContext, drawableId));
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    public List<DayDecorator> getDecoratorsList() {
        return mDecoratorsList;
    }

    public Locale getLocale() {
        return mContext.getResources().getConfiguration().locale;
    }

    public String getCurrentMonth() {
        return CalendarUtility.getCurrentMonth(mCurrentMonthIndex);
    }

    public String getCurrentYear() {
        return String.valueOf(mCalendar.get(Calendar.YEAR));
    }

    public boolean isOverflowDateVisible() {
        return mIsOverflowDateVisible;
    }
}