package com.xinlan.callingshow;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

public class PhoneListenService extends Service {
    private static final String TAG = "PhoneListenService";

    int NOTIFICATION_ID = "PhoneListenService".hashCode();

    // 电话管理者对象
    private TelephonyManager mTelephonyManager;
    // 电话状态监听者
    private MyPhoneCallListener phoneCallListener;

    @Override
    public void onCreate() {
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneCallListener = new MyPhoneCallListener();
        phoneCallListener.setPhoneListener(number -> {
            if(TextUtils.isEmpty(number))
                return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(getApplicationContext())) {
                    callingShowView(number);
                }
            } else {
                callingShowView(number);
            }
        });
        mTelephonyManager.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    private void callingShowView(final String number) {
        WindowsUtils.showPopupWindow(getApplicationContext(), "马上办来电秀\n" + "号码:" + number);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification.Builder builder = null;
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelID = "channelId";
            builder = new Notification.Builder(this, channelID);
            setNotificationChannel(channelID);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher_round).setContentText("来电秀").setOngoing(true)
                .setContentTitle("来电秀").setContentIntent(pendIntent);
        startForeground(NOTIFICATION_ID, builder.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setNotificationChannel(String channelId) {
        final NotificationChannel channel = new NotificationChannel(channelId, "channel",
                NotificationManager.IMPORTANCE_NONE);
        channel.setLightColor(Color.BLUE);
        channel.setLockscreenVisibility(Notification.DEFAULT_VIBRATE);
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.createNotificationChannel(channel);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTelephonyManager.listen(phoneCallListener, PhoneStateListener.LISTEN_NONE);
    }
}
