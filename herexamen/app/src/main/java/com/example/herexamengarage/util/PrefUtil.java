package com.example.herexamengarage.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.herexamengarage.TimerFragment;

public class PrefUtil {
    /* Seconds remaining */
    private static final String SECONDS_REMAINING = "com.example.herexamengarage.seconds_remaining";

    public static Long getSecondsRemaining(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(SECONDS_REMAINING, 0);
    }

    public static void setSecondsRemaining(Long seconds, Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(SECONDS_REMAINING, seconds);
        editor.apply();
    }


    /* Previous timer length */
    private static final String PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.example.herexamengarage.previous_timer_length";

    public static Long getPreviousTimerLengthSeconds(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0);
    }

    public static void setPreviousTimerLengthSeconds(Long seconds, Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds);
        editor.apply();
    }


    /* Timer state */
    private static final String TIMER_STATE_ID = "com.example.herexamengarage.timer_state";

    public static TimerFragment.TimerState getTimerState(Context context){
        int ordinal = PreferenceManager.getDefaultSharedPreferences(context).getInt(TIMER_STATE_ID, 0);
        return TimerFragment.TimerState.values()[ordinal];
    }

    public static void setTimerState(TimerFragment.TimerState state, Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        int ordinal = state.ordinal();
        editor.putInt(TIMER_STATE_ID, ordinal);
        editor.apply();
    }


    /* Alarm set time */
    private static final String ALARM_SET_TIME_ID = "com.example.herexamengarage.background_time";

    public static Long getAlarmSetTime(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getLong(ALARM_SET_TIME_ID, 0);
    }

    public static void setAlarmSetTime(Long time, Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(ALARM_SET_TIME_ID, time);
        editor.apply();
    }


    /* Timer length */
    private static final String TIMER_LENGTH_ID = "com.example.herexamengarage.timer_length";

    public static int getTimerLength(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(TIMER_LENGTH_ID, 10);
    }






}
