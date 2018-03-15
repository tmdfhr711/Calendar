package com.plplim.david.calendar.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.plplim.david.calendar.R;
import com.plplim.david.calendar.activity.MainActivity;

/**
 * Created by OHRok on 2018-03-15.
 */

public class AlarmReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        WakeLockUtil.acquireCpuWakeLock(context);

        //Toast.makeText(context, "Alarm Received!!", Toast.LENGTH_SHORT).show();
        Intent intents = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intents,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_textsms_black_24dp)
                .setContentTitle("일정 1시간 전 알림")
                .setContentText("앱에 들어가 일정을 확인해보세요~^^")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        WakeLockUtil.releaseCpuWakeLock();
    }
}
