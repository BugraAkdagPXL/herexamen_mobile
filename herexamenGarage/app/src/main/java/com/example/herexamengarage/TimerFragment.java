package com.example.herexamengarage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.herexamengarage.util.NotificationUtil;
import com.example.herexamengarage.util.PrefUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
@RequiresApi(api = Build.VERSION_CODES.O)
public class TimerFragment extends Fragment {

    public enum TimerState{
        Stopped, Paused, Running
    }

    private CountDownTimer timer;
    private Long timerLengthSeconds = 0L;
    private TimerState timerState = TimerState.Stopped;
    private Long secondsRemaining = 0L;

    FloatingActionButton fabStart;
    FloatingActionButton fabPause;
    FloatingActionButton fabStop;
    MaterialProgressBar progressBar;
    TextView txtTimer;

    public TimerFragment() {
    }

    public static Long setAlarm(Context context,Long nowSeconds, Long secondsRemaining){
        // * 1000 because we need milliseconds
        long wakeUpTime = (nowSeconds + secondsRemaining) * 1000;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Set alarm
        Intent intent = new Intent(context, timerExpiredReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent);
        PrefUtil.setAlarmSetTime(getNowSeconds(), context);
        return wakeUpTime;
    }

    public static Long getNowSeconds(){
        return Calendar.getInstance().getTimeInMillis() / 1000;
    }

    public static void removeAlarm(Context context){
        Intent intent = new Intent(context, timerExpiredReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        PrefUtil.setAlarmSetTime(0L, context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_timer, container, false);

        /* get buttons */
        fabStart = view.findViewById(R.id.timer_start);
        fabPause = view.findViewById(R.id.timer_pause);
        fabStop = view.findViewById(R.id.timer_stop);
        progressBar = view.findViewById(R.id.progress_countdown);
        txtTimer = view.findViewById(R.id.txtCountdown);

        /* buttons listener */
        fabStart.setOnClickListener(v -> {
            startTimer();
            timerState = TimerState.Running;
            updateButtons();
        });

        fabPause.setOnClickListener(v -> {
            timer.cancel();
            timerState = TimerState.Paused;
            updateButtons();
        });

        fabStop.setOnClickListener(v -> {
            timer.cancel();
            onTimerFinished();

        });

        return view;

    }
    @Override
    public void onPause(){
        super.onPause();
        // if timer is running => cancel
        if(timerState == TimerState.Running){
            timer.cancel();
            long wakeUpTime = setAlarm(getContext(), getNowSeconds(), secondsRemaining);
            NotificationUtil.showTimerRunning(getContext(), wakeUpTime);
        }
        else if(timerState == TimerState.Paused){
            NotificationUtil.showTimerPaused(getContext());
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, getContext());
        PrefUtil.setSecondsRemaining(secondsRemaining, getContext());
        PrefUtil.setTimerState(timerState, getContext());
    }

    private void initTimer(){
        timerState = PrefUtil.getTimerState(getContext());

        // if timer not stopped => use previous timer
        if(timerState == TimerState.Stopped){
            setNewTimerLength();
        }
        else {
            setPreviousTimerLength();
        }

        // if timer is stopped use timer length as remaining
        if(timerState == TimerState.Running || timerState == TimerState.Paused){
            secondsRemaining = PrefUtil.getSecondsRemaining(getContext());
        }
        else {
            secondsRemaining = timerLengthSeconds;
        }

        // get the seconds remaining if the timer didn't finish
        long alarmSetTime = PrefUtil.getAlarmSetTime(getContext());
        if(alarmSetTime > 0){
            secondsRemaining -= getNowSeconds() - alarmSetTime;
        }

        if(secondsRemaining <= 0){
            onTimerFinished();
        }
        else if(timerState == TimerState.Running){
            startTimer();
        }
        updateButtons();
        updateCountdownUI();
    }

    private void onTimerFinished(){
        timerState = TimerState.Stopped;
        setNewTimerLength();
        progressBar.setProgress(0);

        PrefUtil.setSecondsRemaining(timerLengthSeconds, getContext());
        secondsRemaining = timerLengthSeconds;
        updateButtons();
        updateCountdownUI();
    }

    public void startTimer(){
        timerState = TimerState.Running;
        /* Create new timer and start it */
        timer = new CountDownTimer(secondsRemaining * 1000, 1000){
            @Override
            public void onTick(long l) {
                secondsRemaining = l / 1000;
                updateCountdownUI();
            }

            @Override
            public void onFinish(){
                onTimerFinished();
            }
        }.start();
    }

    private void setNewTimerLength(){
        int lengthMinutes = PrefUtil.getTimerLength(getContext());
        timerLengthSeconds = (lengthMinutes * 60L);
        progressBar.setMax(timerLengthSeconds.intValue());
    }

    private void setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(getContext());
        progressBar.setMax(timerLengthSeconds.intValue());
    }

    private void updateCountdownUI(){
        // get minutes
        long minutesUntilFinished = secondsRemaining / 60;

        // get seconds
        long secondsInMinutesUntilFinished = secondsRemaining - minutesUntilFinished * 60;
        String secondsString = Long.toString(secondsInMinutesUntilFinished);

        // show on screen
        if(secondsString.length() == 2){
            txtTimer.setText(minutesUntilFinished + ":" + secondsString);
        }
        else {
            txtTimer.setText(minutesUntilFinished + ":0" + secondsString);
        }
        progressBar.setProgress((int) (timerLengthSeconds - secondsRemaining));

    }

    private void updateButtons(){
        switch (timerState){
            case Running:
                fabStart.setEnabled(false);
                fabPause.setEnabled(true);
                fabStop.setEnabled(true);
                break;
            case Stopped:
                fabStart.setEnabled(true);
                fabPause.setEnabled(false);
                fabStop.setEnabled(false);
                break;
            case Paused:
                fabStart.setEnabled(true);
                fabPause.setEnabled(false);
                fabStop.setEnabled(true);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initTimer();
        removeAlarm(getContext());
        NotificationUtil.hideTimerNotification(getContext());
    }
}