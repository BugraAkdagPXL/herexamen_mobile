package com.example.herexamengarage.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.herexamengarage.AppConstants;
import com.example.herexamengarage.R;
import com.example.herexamengarage.TimerNotificationActionReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;

@RequiresApi(api = Build.VERSION_CODES.O)
public class NotificationUtil {
    /* Static variables */
    private static final String CHANNEL_ID_TIMER = "menu_timer";
    private static final String CHANNEL_NAME_TIMER = "Timer App Timer";
    private static final int TIMER_ID = 0;

    /* Show Notifications */
    public static void showTimerExpired(Context context){
        /* Create intent and set action (start) */
        Intent startIntent = new Intent(context, TimerNotificationActionReceiver.class);
        startIntent.setAction(AppConstants.ACTION_START);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Build notification */
        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);
        nBuilder.setContentTitle("Timer expired!")
        .setContentText("Start again?")
        .addAction(R.drawable.ic_arrow, "Start", pendingIntent);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* Create channel */
        NotificationChannel channel = createNChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true);
        nManager.createNotificationChannel(channel);

        nManager.notify(TIMER_ID,nBuilder.build());
    }

    public static void showTimerRunning(Context context, long wakeUpTime){
        /* Create intent and set action (stop) */
        Intent stopIntent = new Intent(context, TimerNotificationActionReceiver.class);
        stopIntent.setAction(AppConstants.ACTION_STOP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Create intent and set action (pause) */
        Intent pauseIntent = new Intent(context, TimerNotificationActionReceiver.class);
        pauseIntent.setAction(AppConstants.ACTION_PAUSE);
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(context, 0, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Create data format */
        SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);

        /* Build notification */
        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);
        nBuilder.setContentTitle("Timer is running!")
                .setContentText("End: " + df.format(new Date(wakeUpTime)))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop, "Stop", pendingIntent)
                .addAction(R.drawable.ic_pause, "Pause", pendingIntentPause);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* Create channel */
        NotificationChannel channel = createNChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true);
        nManager.createNotificationChannel(channel);

        nManager.notify(TIMER_ID,nBuilder.build());
    }

    public static void showTimerPaused(Context context){
        /* Create intent and set action (resume) */
        Intent resumeIntent = new Intent(context, TimerNotificationActionReceiver.class);
        resumeIntent.setAction(AppConstants.ACTION_RESUME);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /* Build notification */
        NotificationCompat.Builder nBuilder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true);
        nBuilder.setContentTitle("Timer is paused.")
                .setContentText("Resume?")
                .addAction(R.drawable.ic_arrow, "Resume", pendingIntent);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        /* Create channel */
        NotificationChannel channel = createNChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true);
        nManager.createNotificationChannel(channel);

        nManager.notify(TIMER_ID,nBuilder.build());
    }

    /* Hide notification */
    public static void hideTimerNotification(Context context){
        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(TIMER_ID);
    }

    /* Create a notification builder */
    private static NotificationCompat.Builder getBasicNotificationBuilder(Context context, String channelId, boolean playSound) {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_timer)
                .setAutoCancel(true)
                .setDefaults(0);

        /* Do we want sound? */
        if(playSound){
            nBuilder.setSound(notificationSound);
        }

        return nBuilder;

    }

    /* Create a notification channel */
    public static NotificationChannel createNChannel(String channelId, String channelName, boolean playSound){
        int channelImportance;
        if(playSound){
            channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
        }
        else {
            channelImportance = NotificationManager.IMPORTANCE_LOW;
        }
        NotificationChannel nChannel = new NotificationChannel(channelId, channelName, channelImportance);
        nChannel.enableLights(true);
        nChannel.setLightColor(Color.BLUE);
        return nChannel;

    }

}
