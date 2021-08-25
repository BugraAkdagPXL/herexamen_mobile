package com.example.herexamengarage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.herexamengarage.util.NotificationUtil;
import com.example.herexamengarage.util.PrefUtil;
@RequiresApi(api = Build.VERSION_CODES.O)
public class timerExpiredReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationUtil.showTimerExpired(context);
        PrefUtil.setTimerState(TimerFragment.TimerState.Stopped, context);
        PrefUtil.setAlarmSetTime(0L, context);
    }
}