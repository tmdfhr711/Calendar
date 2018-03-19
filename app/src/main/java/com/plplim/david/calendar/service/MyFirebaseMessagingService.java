package com.plplim.david.calendar.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.plplim.david.calendar.R;
import com.plplim.david.calendar.activity.MainActivity;
import com.plplim.david.calendar.model.NotificationModel;
import com.plplim.david.calendar.util.AlarmReceive;
import com.plplim.david.calendar.util.SharedPreferenceUtil;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Map;

/**
 * Created by OHRok on 2018-03-06.
 */

public class MyFirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private SharedPreferenceUtil sharedPreferenceUtil = new SharedPreferenceUtil(this);
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String title = remoteMessage.getData().get("title").toString();
        String text = remoteMessage.getData().get("text").toString();
        String date = remoteMessage.getData().get("date").toString();
        String time = remoteMessage.getData().get("time").toString();
        String sender = remoteMessage.getData().get("sender").toString();
        Log.e("onMessageReceived", sharedPreferenceUtil.getValue("userAuth", ""));
        if (remoteMessage.getData().size() > 0) {
            if (sender.equals(sharedPreferenceUtil.getValue("userAuth", ""))) {
                registerAlarm(date, time);
            } else {
                sendNotification(title, text);
                registerAlarm(date, time);
            }
        } else {
            if (sender.equals(sharedPreferenceUtil.getValue("userAuth", ""))) {
                registerAlarm(date, time);
            } else {
                sendNotification(title, text);
                registerAlarm(date, time);
            }

        }
    }

    private void sendNotification(String title, String text) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_textsms_black_24dp)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void registerAlarm(String date, String time) {
        Log.e("registerAlarm", "date : " + date);
        Log.e("registerAlarm", "time : " + time);

        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceive.class);
        PendingIntent pender = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        //받아온 데이터
        int year, month, day, hour, minute;

        String[] getDate = date.split("/");
        String[] getTime = time.split(":");
        year = Integer.parseInt(getDate[0]);
        month = Integer.parseInt(getDate[1]) - 1;   //Calendar 에서 1월은 0으로 시작함
        day = Integer.parseInt(getDate[2]);
        hour = Integer.parseInt(getTime[0]);
        minute = Integer.parseInt(getTime[1]);

        Log.e("RegisterAlarm", "Now : " + String.valueOf(calendar.get(Calendar.YEAR)) + "/" + String.valueOf(calendar.get(Calendar.MONTH)) +
                "/" + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + "/" + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":" + String.valueOf(calendar.get(Calendar.MINUTE)));
        Log.e("RegisterAlarm", "getDate : " + String.valueOf(year) + "/" + String.valueOf(month) +
                "/" + String.valueOf(day) + "/" + String.valueOf(hour) + ":" + String.valueOf(minute));

        //날짜가 오늘보다 이전일 경우
        if (calendar.get(Calendar.YEAR) <= year && calendar.get(Calendar.MONTH) <= month && calendar.get(Calendar.DAY_OF_MONTH) <= day) {
            //현재시간보다 이전일 경우만 알람등록
            if(calendar.get(Calendar.HOUR_OF_DAY) <= hour && calendar.get(Calendar.MINUTE) < minute) {
                calendar.set(year, month, day, hour, minute);

                //알림을 한 시간 전에 설정하기
                if(hour == 0){
                    //hour를 23(오후 11시)로 설정 한뒤 calendar.set(hour - 1)
                } else {
                    //그냥 그대로 진행 calendar.set(hour)
                }
                alarm.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pender);

            }
        }
    }
}
