package com.example.herexamengarage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.herexamengarage.util.NotificationUtil;
import com.example.herexamengarage.util.PrefUtil;
@RequiresApi(api = Build.VERSION_CODES.O)
public class TimerNotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long secondsRemaining;
        long wakeUpTime;
        switch (intent.getAction()){
            case AppConstants.ACTION_STOP:
                // stop alarm and set timer to stopped
                TimerFragment.removeAlarm(context);
                PrefUtil.setTimerState(TimerFragment.TimerState.Stopped, context);

                // hide notification
                NotificationUtil.hideTimerNotification(context);
                break;

            case AppConstants.ACTION_PAUSE:
                // get seconds remaining and save this in preferences, also remove alarm and set state to paused
                secondsRemaining = PrefUtil.getSecondsRemaining(context);
                long alarmSetTime = PrefUtil.getAlarmSetTime(context);
                long nowSeconds = TimerFragment.getNowSeconds();
                secondsRemaining -= nowSeconds - alarmSetTime;
                PrefUtil.setSecondsRemaining(secondsRemaining,context);
                TimerFragment.removeAlarm(context);
                PrefUtil.setTimerState(TimerFragment.TimerState.Paused, context);

                // show other notification
                NotificationUtil.showTimerPaused(context);
                break;

            case AppConstants.ACTION_RESUME:
                // get seconds remaining and set alarm with that
                secondsRemaining = PrefUtil.getSecondsRemaining(context);
                wakeUpTime = TimerFragment.setAlarm(context, TimerFragment.getNowSeconds(), secondsRemaining);
                PrefUtil.setTimerState(TimerFragment.TimerState.Running, context);

                // show notification
                NotificationUtil.showTimerRunning(context, wakeUpTime);
                break;

            case AppConstants.ACTION_START:
                // get minutes remaining and use that to calculate seconds and set new alarm and set state to running
                long minutesRemaining = PrefUtil.getTimerLength(context);
                secondsRemaining = minutesRemaining *60L;
                wakeUpTime = TimerFragment.setAlarm(context, TimerFragment.getNowSeconds(), secondsRemaining);
                PrefUtil.setTimerState(TimerFragment.TimerState.Running, context);
                PrefUtil.setSecondsRemaining(secondsRemaining, context);

                // show notification
                NotificationUtil.showTimerRunning(context, wakeUpTime);
                break;

        }
    }
}